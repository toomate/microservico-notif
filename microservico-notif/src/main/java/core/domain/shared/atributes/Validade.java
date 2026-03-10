package core.domain.shared.atributes;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
public class Validade {
    private final LocalDate dataValidade;
    private final Boolean gravidade;

    public Validade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
        this.gravidade = !dataValidade.isAfter(LocalDate.now());
    }

}
