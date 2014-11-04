package net.johnewart.kensho.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.johnewart.shuzai.TimeSeries;
import org.joda.time.DateTime;

import java.io.IOException;
import java.math.BigDecimal;

public class CustomTimeSeriesSerializer extends JsonSerializer<TimeSeries> {

    @Override
    public void serialize(TimeSeries timeSeries, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart("times");
        for(DateTime dt : timeSeries.index()) {
            jsonGenerator.writeNumber(dt.getMillis());
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeArrayFieldStart("values");
        for(BigDecimal value : timeSeries.values()) {
            jsonGenerator.writeNumber(value);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
