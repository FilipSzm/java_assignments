package uj.java.annotations;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes({"uj.java.annotations.MyComparable"})
public class MyProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            annotatedElements.forEach(this::processElement);
        }
        return true;
    }

    private void processElement(Element element) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Processing " + element);
        TypeElement clazz = (TypeElement) element;
        String className = clazz.getQualifiedName().toString();

        try {
            createFile(clazz, className);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createFile(TypeElement clazz, String className) throws IOException {
        JavaFileObject file = processingEnv.getFiler().createSourceFile(className + "Comparator");
        String packageName = packageName(className);
        int tabs = 0;
        try (PrintWriter out = new PrintWriter(file.openWriter())) {
            if (packageName != null)
                tabbedWrite("package " + packageName + ";\n", out, tabs);

            tabs = writeStart(clazz, out, tabs);

            int finalTabs = tabs; //ponieważ ilość tabów się nie zmienia podczas tej operacji
            clazz.getEnclosedElements()
                    .stream()
                    .filter(e -> e.asType().getKind().isPrimitive() && !e.getModifiers().contains(Modifier.PRIVATE))
                    .sorted(priorityComparator)
                    .forEach(e -> writeComparison(e, out, finalTabs));

            writeEnd(out, tabs);
        }
    }

    private int writeStart(TypeElement clazz, PrintWriter out, int tabs) {
        tabbedWrite("public class " + clazz.getSimpleName() + "Comparator {", out, tabs);
        tabs++;
        tabbedWrite("public int compare(" + clazz.getSimpleName() + " o1, " + clazz.getSimpleName() + " o2) {", out, tabs);
        tabs++;
        tabbedWrite("int result = 0;", out, tabs);
        return tabs;
    }

    private void writeEnd(PrintWriter out, int tabs) {
        tabbedWrite("return result;", out, tabs);
        tabs--;
        tabbedWrite("}", out, tabs);
        tabs--;
        tabbedWrite("}", out, tabs);
    }

    private void writeComparison(Element e, PrintWriter out, int tabs) {
        VariableElement prim = (VariableElement) e;
        String type = processingEnv.getTypeUtils().boxedClass(processingEnv.getTypeUtils().getPrimitiveType(prim.asType().getKind())).getSimpleName().toString();
        String name = prim.getSimpleName().toString();
        tabbedWrite("result = " + type + ".compare(o1." + name + ", o2." + name + ");", out, tabs);
        tabbedWrite("if (result != 0) return result;", out, tabs);
    }

    private void tabbedWrite(String text, PrintWriter out, int tabs) {
        for (int i = 0; i < tabs; i++)
            out.write("\t");

        out.write(text);
        out.write("\n");
    }

    private String packageName(String className) {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        return packageName;
    }

    private final static Comparator<Element> priorityComparator = (o1, o2) -> {
        ComparePriority p1 = o1.getAnnotation(ComparePriority.class);
        ComparePriority p2 = o2.getAnnotation(ComparePriority.class);

        if (p1 != null && p2 != null) return p1.value() - p2.value();
        if (p1 != null) return -1;
        if (p2 != null) return 1;
        return 0;
    };
}
