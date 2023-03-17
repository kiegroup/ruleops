package org.drools.ruleops.utils.drl2yaml;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExpressionDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.parser.DrlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

public class Generator {
    private static final Logger LOG = LoggerFactory.getLogger(Generator.class);
    private static final DrlParser drlParser = new DrlParser();
    private static final ObjectMapper mapper;
    static {
        YAMLFactory yamlFactory = YAMLFactory.builder()
                .enable(Feature.MINIMIZE_QUOTES)
                .build();
        mapper = new ObjectMapper(yamlFactory);
        SimpleModule module = new SimpleModule("MySerializer")
            .addSerializer(new PackageDescrSerializer(PackageDescr.class))
            .addSerializer(new ImportDescrSerializer(ImportDescr.class))
            .addSerializer(new RuleDescrSerializer(RuleDescr.class))
            .addSerializer(new PatternDescrSerializer(PatternDescr.class))
            .addSerializer(new ExpressionDescrSerializer(ExpressionDescr.class))
            .addSerializer(new FromDescrSerializer(FromDescr.class))
            .addSerializer(new MVELExprDescrSerializer(MVELExprDescr.class))
            .addSerializer(new FunctionDescrSerializer(FunctionDescr.class))
            .addSerializer(new NotDescrSerializer(NotDescr.class))
            .addSerializer(new AndDescrSerializer(AndDescr.class))
            .addSerializer(new ExistsDescrSerializer(ExistsDescr.class));
        mapper.registerModule(module);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Missing one argument, the project.basedir");
            System.exit(-1);
        }
        final String baseDir = args[0];
        LOG.info("Using basedir: {}",  baseDir);

        Path.of(baseDir);
        Files.walk(Path.of(baseDir).toAbsolutePath())
            .filter(Files::isRegularFile)
            .filter(p -> p.getFileName().toString().endsWith(".drl"))
            .forEach(p -> convertFile(p.toAbsolutePath()));
    }

    public static void convertFile(Path drlFile) {
        try {
            String drlTxt = Files.readAllLines(drlFile).stream().collect(Collectors.joining("\n"));
            Path to = Path.of(drlFile.toString() + ".yaml");
            LOG.info("writing to: {}", to);
            String fileContent = "# Automatically generated from: " + drlFile.toFile().getName().toString() + "\n" + convert(drlTxt);
            Files.writeString(to, fileContent, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String convert(String drlTxt) throws Exception {
        PackageDescr descr = drlParser.parse(null, drlTxt);
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, descr);
        return writer.toString();
    }
}
