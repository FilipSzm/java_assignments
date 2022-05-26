package uj.java.w7.insurance;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import static java.lang.Integer.min;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;

public class FloridaInsurance {
    private static List<InsuranceEntry> entries;

    public static void main(String[] args) {
        readInsuranceData();

        count();
        tiv2012();
        most_valuable();
    }

    private static void count() {
        writeToFile(
                "count.txt",
                String.valueOf(entries.stream().map(InsuranceEntry::county).distinct().count())
        );
    }

    private static void tiv2012() {
        writeToFile(
                "tiv2012.txt",
                String.valueOf(entries.stream().map(InsuranceEntry::tiv_2012).reduce(BigDecimal.ZERO, BigDecimal::add))
        );
    }

    private static void most_valuable() {
        writeToFile("most_valuable.txt", "country,value\n" +
                entries
                .stream()
                .map(entry -> new Pair<>(entry.county(), entry.tiv_2012().subtract(entry.tiv_2011())))
                .collect(CustomCollector.customCollector())
        );
    }

    private static void writeToFile(String fileName, String data) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))) {
            bufferedWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void readInsuranceData() {
        try (ZipFile zipFile = new ZipFile("FL_insurance.csv.zip");
             BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(
                             zipFile.getInputStream(
                                     zipFile.getEntry("FL_insurance.csv"))))
        ) {
            entries = bufferedReader
                    .lines().skip(1)
                    .map(entry -> setInsuranceEntry(entry.split(",")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static InsuranceEntry setInsuranceEntry(String[] data) {
        return new InsuranceEntry(
                new BigInteger(data[0]),
                data[1],
                data[2],
                new BigDecimal(data[3]),
                new BigDecimal(data[4]),
                new BigDecimal(data[5]),
                new BigDecimal(data[6]),
                new BigDecimal(data[7]),
                new BigDecimal(data[8]),
                Double.parseDouble(data[9]),
                Double.parseDouble(data[10]),
                Double.parseDouble(data[11]),
                Double.parseDouble(data[12]),
                new BigDecimal(data[13]),
                new BigDecimal(data[14]),
                data[15],
                data[16],
                Integer.parseInt(data[17])
        );
    }
}

record Pair<A, B>(A country, B value) { }

class PairComparator<A, B extends Comparable<B>> implements Comparator<Pair<A, B>> {

    @Override
    public int compare(Pair<A, B> o1, Pair<A, B> o2) {
        return o2.value().compareTo(o1.value());
    }
}

class CustomCollector implements Collector<Pair<String, BigDecimal>, Map<String, BigDecimal>, String> {

    public static CustomCollector customCollector() {
        return new CustomCollector();
    }

    @Override
    public Supplier<Map<String, BigDecimal>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<String, BigDecimal>, Pair<String, BigDecimal>> accumulator() {
        return (acc, x) -> acc.put(x.country(), x.value().add(acc.getOrDefault(x.country(), BigDecimal.ZERO)));
    }

    @Override
    public BinaryOperator<Map<String, BigDecimal>> combiner() {
        return (acc1, acc2) -> {
            acc2.forEach((k, v) -> acc1.merge(k, v, BigDecimal::add));
            return acc1;
        };
    }

    @Override
    public Function<Map<String, BigDecimal>, String> finisher() {
        return (acc) -> {
            List<Pair<String, BigDecimal>> list = new LinkedList<>();
            for (var entry : acc.entrySet()) {
                list.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
            list.sort(new PairComparator<>());
            List<String> out = new LinkedList<>();
            list.subList(0, min(10, list.size())).forEach(x -> out.add(x.country() + "," + x.value()));
            return String.join("\n", out);
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>(Arrays.asList(UNORDERED, CONCURRENT));
    }
}