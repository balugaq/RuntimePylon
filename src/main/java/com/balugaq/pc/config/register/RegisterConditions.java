package com.balugaq.pc.config.register;

import com.balugaq.pc.config.ConfigReader;
import com.balugaq.pc.config.Deserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * @author balugaq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@NullMarked
public class RegisterConditions implements Deserializer<RegisterConditions> {
    private List<RegisterCondition> conditions;

    @Override
    public List<ConfigReader<?, RegisterConditions>> readers() {
        return List.of(
                ConfigReader.of(String.class, s -> new RegisterConditions(List.of(Deserializer.REGISTER_CONDITION.deserialize(s)))),
                ConfigReader.of(
                        List.class, lst -> {
                            List<RegisterCondition> conditions = new ArrayList<>();
                            for (Object o : lst) {
                                if (o instanceof String) {
                                    conditions.add(Deserializer.REGISTER_CONDITION.deserialize(o));
                                }
                            }
                            return new RegisterConditions(conditions);
                        }
                )
        );
    }

    public boolean pass() {
        for (RegisterCondition condition : conditions) {
            if (!condition.pass()) return false;
        }
        return true;
    }
}
