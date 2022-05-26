package uj.java.w3;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface JsonMapper {

    String toJson(Map<String, ?> map);

    static JsonMapper defaultInstance() {
        return new JsonMapperImplementation();
    }

}

class JsonMapperImplementation implements JsonMapper {

    @Override
    public String toJson(Map<String, ?> map) {
        return recursiveMap(map).toString();
    }

    private StringBuilder recursiveMap(Map<String, ?> map) {
        StringBuilder output = new StringBuilder("{");
        if (!(map == null)) {
            Iterator<String> iter = map.keySet().iterator();
            while (iter.hasNext()) {
                var key = iter.next();
                output.append('\"').append(key).append("\":");
                output.append(recursiveValue(map.get(key)));
                if (iter.hasNext())
                    output.append(',');
            }
        }
        return output.append('}');
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> StringBuilder recursiveValue(T data) {
        StringBuilder output = new StringBuilder();

        if (data.getClass().equals(String.class))
            output.append('\"').append(((String) data).replace("\"", "\\\"")).append("\"");
        else if (data instanceof List)
            output.append('[').append(listManager((List) data)).append(']');
        else if (data instanceof Map)
            output.append(recursiveMap((Map) data));
        else
            output.append(data);

        return output;
    }

    private <T> StringBuilder listManager(List<T> list) {
        StringBuilder output = new StringBuilder();

        Iterator<T> iter = list.iterator();
        while (iter.hasNext()) {
            var data = iter.next();
            output.append(recursiveValue(data));
            if (iter.hasNext())
                output.append(',');
        }
        return output;
    }
}
