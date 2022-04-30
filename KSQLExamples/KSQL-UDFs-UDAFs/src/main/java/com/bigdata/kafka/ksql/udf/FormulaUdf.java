package com.bigdata.kafka.ksql.udf;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

import org.apache.kafka.common.Configurable;

import java.util.Map;

@UdfDescription(name = "formula",
        author = "example user",
        version = "1.0.2",
        description = "A custom formula for important business logic.")
public class FormulaUdf implements Configurable {

    private int baseValue;

    @Override
    public void configure(final Map<String, ?> map) {
        String s = (String) map.get("ksql.functions.formula.base.value");
        baseValue = Integer.parseInt(s);
    }

    @Udf(description = "The standard version of the formula with integer parameters.")
    public long formula(@UdfParameter(value = "v1", description = "V1 Value") final int v1, @UdfParameter(value = "v2", description = "V2 Value") final int v2) {
        return (v1 * v2) + baseValue;
    }

    @Udf(description = "A special variant of the formula, handling double parameters.")
    public long formula(@UdfParameter(value = "v1", description = "V1 Value") final double v1, @UdfParameter(value = "v2", description = "V2 Value") final double v2) {
        return ((int) (Math.ceil(v1) * Math.ceil(v2))) + baseValue;
    }

}
