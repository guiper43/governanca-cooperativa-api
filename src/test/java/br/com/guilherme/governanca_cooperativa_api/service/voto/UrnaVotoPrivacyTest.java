package br.com.guilherme.governanca_cooperativa_api.service.voto;

import br.com.guilherme.governanca_cooperativa_api.domain.entity.UrnaVoto;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertFalse;

class UrnaVotoPrivacyTest {

    @Test
    void urnaVoto_naoDevePossuirColunasDeVinculoComEleitor() {
        Field[] fields = UrnaVoto.class.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName().toLowerCase();
            assertFalse(name.contains("associado"), "A UrnaVoto não deve possuir referência direta ao associado: " + field.getName());
            assertFalse(name.contains("cpf"), "A UrnaVoto não deve possuir referência direta ao CPF: " + field.getName());
            assertFalse(name.contains("token"), "A UrnaVoto não deve possuir referência a tokens: " + field.getName());
            assertFalse(name.contains("participacao"), "A UrnaVoto não deve possuir referência ao registro de participação: " + field.getName());
            assertFalse(name.contains("eleitor"), "A UrnaVoto não deve possuir referência ao eleitor: " + field.getName());
            assertFalse(name.contains("usuario"), "A UrnaVoto não deve possuir referência a usuário: " + field.getName());
        }
    }
}
