package core.domain.utility;

import core.domain.notification.NotificacaoAgnostico;
import core.domain.notification.adapter.NotificacaoDeterminada;
import core.domain.notification.adapter.TipoNotificacao;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationResolver {

    public NotificacaoDeterminada resolve(NotificacaoAgnostico notification) {
        Object payload = notification.payload();
        for (TipoNotificacao tipo : TipoNotificacao.values()) {
            if (notification.id().contains(tipo.getId())) {
                Class<? extends NotificacaoDeterminada> clazz = tipo.getClazz();
                try {
                    // If payload already is the concrete class, return it (cast)
                    if (clazz.isInstance(payload)) {
                        NotificacaoDeterminada instance = clazz.cast(payload);
                        setCommonFields(instance, notification);
                        return instance;
                    }

                    // Otherwise, try to instantiate and populate fields from a Map payload
                    NotificacaoDeterminada instance = clazz.getDeclaredConstructor().newInstance();

                    // set common fields (id, timestamp) defined in NotificacaoDeterminada
                    setCommonFields(instance, notification);

                    if (payload instanceof Map<?, ?> mapPayload) {
                        List<Field> fields = getAllFields(clazz);
                        for (Field field : fields) {
                            String name = field.getName();
                            // skip common fields already set
                            if ("id".equals(name) || "timestamp".equals(name)) continue;

                            if (mapPayload.containsKey(name)) {
                                Object rawValue = mapPayload.get(name);
                                Object converted = convertValue(rawValue, field.getType());
                                field.setAccessible(true);
                                field.set(instance, converted);
                            }
                        }
                    }

                    return instance;
                } catch (Exception e) {
                    throw new RuntimeException("Falha ao resolver notificação do tipo " + tipo.name(), e);
                }
            }
        }
        throw new IllegalArgumentException("Tipo de notificação desconhecido para id: " + notification.id());
    }

    // Seta campos comuns id e timestamp se existirem na hierarquia
    private void setCommonFields(NotificacaoDeterminada instance, NotificacaoAgnostico notification) {
        try {
            // Tenta setar no tipo base primeiro, se existir
            try {
                Field idField = NotificacaoDeterminada.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(instance, notification.id());
            } catch (NoSuchFieldException ignored) {
                // fallback: tenta localizar em qualquer ponto da hierarquia, se presente
                Field idField = findFieldInHierarchy(instance.getClass(), "id");
                if (idField != null) {
                    idField.setAccessible(true);
                    idField.set(instance, notification.id());
                }
            }

            try {
                Field tsField = NotificacaoDeterminada.class.getDeclaredField("timestamp");
                tsField.setAccessible(true);
                tsField.set(instance, notification.timestamp());
            } catch (NoSuchFieldException ignored) {
                Field tsField = findFieldInHierarchy(instance.getClass(), "timestamp");
                if (tsField != null) {
                    tsField.setAccessible(true);
                    tsField.set(instance, notification.timestamp());
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Não foi possível setar campos comuns na notificação", e);
        }
    }

    private Field findFieldInHierarchy(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field f = current.getDeclaredField(fieldName);
                return f;
            } catch (NoSuchFieldException ignored) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field f : current.getDeclaredFields()) {
                fields.add(f);
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    private Object convertValue(Object raw, Class<?> targetType) {
        if (raw == null) return null;
        // direct assignable
        if (targetType.isInstance(raw)) return raw;

        // If raw is a Map and targetType is a complex class, attempt recursive mapping
        if (raw instanceof Map<?, ?> mapRaw) {
            // skip primitives, wrappers, String, enums
            if (!targetType.isPrimitive()
                    && !targetType.equals(String.class)
                    && !Number.class.isAssignableFrom(targetType)
                    && !targetType.isEnum()
                    && !Modifier.isAbstract(targetType.getModifiers())
                    && !targetType.isInterface()) {
                Object mapped = mapToObject(mapRaw, targetType);
                if (mapped != null) return mapped;
            }
        }

        // handle numbers
        if (Number.class.isAssignableFrom(targetType) || targetType.isPrimitive()) {
            if (raw instanceof Number n) {
                return convertNumber(n, targetType);
            }
            if (raw instanceof String s) {
                try {
                    if (targetType == Integer.class || targetType == int.class) return Integer.valueOf(s);
                    if (targetType == Long.class || targetType == long.class) return Long.valueOf(s);
                    if (targetType == Double.class || targetType == double.class) return Double.valueOf(s);
                    if (targetType == Float.class || targetType == float.class) return Float.valueOf(s);
                    if (targetType == Short.class || targetType == short.class) return Short.valueOf(s);
                    if (targetType == Byte.class || targetType == byte.class) return Byte.valueOf(s);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // handle LocalDateTime
        if (targetType == LocalDateTime.class) {
            if (raw instanceof String s) {
                try {
                    return LocalDateTime.parse(s);
                } catch (DateTimeParseException ex) {
                    // try as epoch millis
                    try {
                        long millis = Long.parseLong(s);
                        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), java.time.ZoneId.systemDefault());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if (raw instanceof Number n) {
                long millis = n.longValue();
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), java.time.ZoneId.systemDefault());
            }
        }

        // fallback: try to convert via toString for primitives/strings
        if (targetType == String.class) {
            return raw.toString();
        }

        // If target type is enum
        if (targetType.isEnum() && raw instanceof String s) {
            Object[] constants = targetType.getEnumConstants();
            for (Object c : constants) {
                if (c.toString().equalsIgnoreCase(s)) return c;
            }
        }

        // No conversion found
        return null;
    }

    private Object mapToObject(Map<?, ?> mapRaw, Class<?> targetType) {
        try {
            Object instance = targetType.getDeclaredConstructor().newInstance();
            List<Field> fields = getAllFields(targetType);
            for (Field field : fields) {
                String name = field.getName();
                if (mapRaw.containsKey(name)) {
                    Object rawValue = mapRaw.get(name);
                    Object converted = convertValue(rawValue, field.getType());
                    field.setAccessible(true);
                    field.set(instance, converted);
                }
            }
            return instance;
        } catch (Exception e) {
            // couldn't instantiate or set fields - return null to let caller attempt other strategies
            return null;
        }
    }

    private Object convertNumber(Number n, Class<?> targetType) {
        if (targetType == Integer.class || targetType == int.class) return n.intValue();
        if (targetType == Long.class || targetType == long.class) return n.longValue();
        if (targetType == Double.class || targetType == double.class) return n.doubleValue();
        if (targetType == Float.class || targetType == float.class) return n.floatValue();
        if (targetType == Short.class || targetType == short.class) return n.shortValue();
        if (targetType == Byte.class || targetType == byte.class) return n.byteValue();
        return n;
    }

}
