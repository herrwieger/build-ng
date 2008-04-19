package org.buildng.elegant.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;

public class FluidGenerator {

    // --------------------------------------------------------------------------
    // constants
    // --------------------------------------------------------------------------

    private static final String ELEGANT_BUILDER       = "ElegantBuilder";
    private static final String TASK_BUILDER_SUFFIX   = "TaskBuilder";
    private static final String TYPE_BUILDER_SUFFIX   = "TypeBuilder";


    private static final String ELEGANT_PACKAGE       = "org.buildng.elegant";
    private static final String ELEGANT_TYPE_PACKAGE  = "org.buildng.elegant.type";

    private static Set<String>  JAVA_KEYWORDS         = new HashSet<String>();
    static {
        JAVA_KEYWORDS.add("package");
        JAVA_KEYWORDS.add("import");
        JAVA_KEYWORDS.add("class");
        JAVA_KEYWORDS.add("public");
        JAVA_KEYWORDS.add("protected");
        JAVA_KEYWORDS.add("private");
        JAVA_KEYWORDS.add("if");
        JAVA_KEYWORDS.add("default");
        JAVA_KEYWORDS.add("static");
    }



    // --------------------------------------------------------------------------
    // class variables
    // --------------------------------------------------------------------------

    private static final Logger LOG                   = Logger.getLogger(FluidGenerator.class);



    //--------------------------------------------------------------------------  
    // main methods
    //--------------------------------------------------------------------------

    public static void main(String[] pArgs) {
        new FluidGenerator().generateElegant(new File(pArgs[0]));
    }
    
    
    // --------------------------------------------------------------------------
    // instance methods
    // --------------------------------------------------------------------------

    public void generateElegant(File pJavaGenDir) {
        Properties defaults = getAntTaskDefaults();
        try {
            File dir = new File(pJavaGenDir, getPathForPackage(ELEGANT_PACKAGE));
            dir.mkdirs();
            FileWriter writer = new FileWriter(new File(dir, ELEGANT_BUILDER + ".java"));
            writer.write("package " + ELEGANT_PACKAGE + ";\n");
            writer.write("import " + ELEGANT_TYPE_PACKAGE + ".*;\n");
            writer.write("public class " + ELEGANT_BUILDER + " extends ElegantBuilderBase {\n");
            writer.write("    public ElegantBuilder(java.io.File pBaseDir) {\n");
            writer.write("    super(pBaseDir);\n");
            writer.write("    }\n");

            Set<String>   builderMethodNames = new HashSet<String>();
            Set<Class<?>> dataTypes          = new HashSet<Class<?>>();
            for (Iterator taskIterator = defaults.entrySet().iterator(); taskIterator.hasNext();) {
                Entry  entry         = (Entry) taskIterator.next();
                String taskClassName = (String) entry.getValue();
                Class  taskClass;
                try {
                    taskClass = Class.forName(taskClassName);
                } catch (ClassNotFoundException ex) {
                    continue;
                }
                String taskName          = ((String) entry.getKey());
                String fluidMethodName   = taskName.replace("-", "_");
                String builderName       = getBuilderClassName(taskClass);
                String builderMethodName = getCompilableMethodName(fluidMethodName);
                writer.write("public " + builderName + " " + builderMethodName + "() {\n");
                writer.write("    " + taskClass.getName() + " task = (" + taskClass.getName()
                        + ")getProject().createTask(\"" + taskName + "\");\n");
                writer.write("    return new " + builderName + "(this, task);\n");
                writer.write("}\n");
                builderMethodNames.add(builderMethodName);

                createBuilder(taskClass, dataTypes, dir);
            }
            createDataTypeBuilders(dataTypes, builderMethodNames, writer, pJavaGenDir);
            writer.write("}\n");
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Properties getAntTaskDefaults() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/defaults.properties"));
        } catch (IOException ex) {
            throw new RuntimeException("ex");
        }
        return properties;
    }


    private void createBuilder(Class<?> pTaskClass, Set<Class<?>> pDataTypes, File pDir) throws IOException {
        final String builderName      = getBuilderClassName(pTaskClass);
        String       elementClassname = pTaskClass.getCanonicalName();
        LOG.debug(builderName + "::" + elementClassname);

        final FileWriter writer = new FileWriter(new File(pDir, builderName + ".java"));
        writer.write("package " + ELEGANT_PACKAGE + ";\n");
        writer.write("import " + ELEGANT_TYPE_PACKAGE + ".*;\n");

        writer.write("public class " + builderName + " implements TaskTaskBuilder {\n");

        writer.write("private " + ELEGANT_BUILDER + " fBuilder;\n");
        writer.write("private " + elementClassname + " fElement;\n");

        writer.write("public " + builderName + "(" + ELEGANT_BUILDER + " pBuilder, " + elementClassname
                + " pElement){\n");
        writer.write("  fBuilder = pBuilder;\n");
        writer.write("  fElement = pElement;\n");
        writer.write("}\n");
        writer.write("public " + elementClassname + " get(){return fElement;}\n");
        writer.write("public void execute(){fElement.execute();}\n");
        writer.write("public " + Project.class.getName() + " getProject(){return fBuilder.getProject();}\n");

        final List<Method> fileSetters = new ArrayList<Method>();
        new AbstractMethodVisitor() {

            @Override
            void visitSetter(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                String content = "fElement." + pMethod.getName() + "(pBuilder.get());\n";
                writeNestedElementMethod(builderName, pFluidMethodName, pFirstArgumentType,
                        pMethod.getExceptionTypes(), content, writer);
            }

            @Override
            void visitPlainSetter(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                try {
                    writer.write("public " + builderName + " " + pFluidMethodName + "("
                            + pFirstArgumentType.getCanonicalName() + " pValue)"
                            + getThrowsDeclaration(pMethod.getExceptionTypes()) + "{\n");
                    writer.write("  fElement." + pMethod.getName() + "(pValue);\n");
                    writer.write("  return this;\n");
                    writer.write("}\n");

                    if (pFirstArgumentType.equals(File.class)) {
                        fileSetters.add(pMethod);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            
            @Override
            void visitCreateMethod(Method pMethod, String pFluidMethodName, Class<?> pReturnType) {
                String content;
                if (isAntType(pReturnType)) {
                    content = "pBuilder.apply(fElement." + pMethod.getName() + "());\n";
                } else {
                    content = "/*TODO*/";
                }
                writeNestedElementMethod(builderName, pFluidMethodName, pMethod.getReturnType(),
                        pMethod.getExceptionTypes(), content, writer);
            }

            @Override
            void visitAddConfiguredMethod(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                String content = "fElement." + pMethod.getName() + "(pBuilder.get());\n";
                writeNestedElementMethod(builderName, pFluidMethodName, pFirstArgumentType,
                        pMethod.getExceptionTypes(), content, writer);
            }

            @Override
            void visitAddMethod(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                String content = "fElement." + pMethod.getName() + "(pBuilder.get());\n";
                writeNestedElementMethod(builderName, pFluidMethodName, pFirstArgumentType,
                        pMethod.getExceptionTypes(), content, writer);
            }
        }.visit(pTaskClass, pDataTypes);

        generateStringToFileSetterMethods(pTaskClass, builderName, fileSetters, writer);

        writer.write("}\n");
        writer.close();
    }

    private boolean isCorrespondingStringMethodAlreadyDeclared(Class<?> pTaskClass, Method pMethod) {
        try {
            pTaskClass.getMethod(pMethod.getName(), new Class<?>[] { String.class });
            return true;
        } catch (Exception ex) {
            return false;
        }
    }




    static boolean hasNoCorrespondingAddConfiguredMethod(Class<?> pElementClass, String pMethodName,
            Class<?> pFirstArgumentType) {
        String methodName = AbstractMethodVisitor.ADD_CONFIGURED_PREFIX + getStringWithoutPrefix(pMethodName, AbstractMethodVisitor.ADD_PREFIX);
        try {
            Method foundMethod = pElementClass.getMethod(methodName, new Class<?>[] { pFirstArgumentType });
            return !isFluidAddMethod(foundMethod.getParameterTypes()[0], foundMethod.getReturnType(), 1);
        } catch (Exception ex) {
            return true;
        }
    }

    static boolean isFluidAddMethod(Class<?> firstArgumentType, Class<?> returnType, int numParameters) {
        return (java.lang.Void.TYPE.equals(returnType) && numParameters == 1
                && !java.lang.String.class.equals(firstArgumentType) && !firstArgumentType.isArray()
                && !firstArgumentType.isPrimitive() && !firstArgumentType.isInterface());
        // TODO check, whether we need the test isInterface
    }


    private void writeNestedElementMethod(String pBuilderName, String pFluidMethodname, Class<?> pNestedTypeClass,
            Class<?>[] pExceptionTypes, String pContent, FileWriter pWriter) {

        try {
            pWriter.write("public " + pBuilderName + " " + pFluidMethodname + "("
                    + getBuilderClassName(pNestedTypeClass) + " pBuilder)" + getThrowsDeclaration(pExceptionTypes)
                    + "{\n");
            pWriter.write(pContent);
            pWriter.write("    return this;\n");
            pWriter.write("}\n");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String getThrowsDeclaration(Class<?>[] pExceptionTypes) {
        if (pExceptionTypes.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(" throws ");
        for (Class<?> exception : pExceptionTypes) {
            builder.append(exception.getCanonicalName());
            builder.append(", ");
        }
        builder.setLength(builder.length() - ", ".length());
        return builder.toString();
    }

    private void createDataTypeBuilders(Set<Class<?>> pDataTypes, Set<String> pBuilderMethodNames,
            FileWriter pElegantWriter, File pJavaGenDir) throws IOException {
        File dir = new File(pJavaGenDir, getPathForPackage(ELEGANT_TYPE_PACKAGE));
        dir.mkdirs();

        Set<Class<?>> discoveredTypes = new HashSet<Class<?>>(pDataTypes);
        Set<Class<?>> builtTypes      = new HashSet<Class<?>>();
        Set<Class<?>> typesToBuild    = pDataTypes;
        do {
            for (Class<?> dataType : typesToBuild) {
                if (AbstractMethodVisitor.needsABuilder(dataType) && isAntType(dataType)) {
                    createDataTypeBuilder(dataType, discoveredTypes, dir);
                    String builderClassName = getBuilderClassName(dataType);
                    String fluidMethodName  = StringUtils.uncapitalize(getBuilderClassPrefix(dataType));
                    if (isInConflictWithTaskCreationMethod(fluidMethodName, pBuilderMethodNames)) {
                        fluidMethodName = "_" + fluidMethodName;
                    }
                    pElegantWriter.write("public " + builderClassName + " " + fluidMethodName + "(){" + "return new "
                            + builderClassName + "(this);" + "}\n");
                }
            }
            builtTypes.addAll(typesToBuild);
            typesToBuild = new HashSet<Class<?>>(discoveredTypes);
            typesToBuild.removeAll(builtTypes);
        } while (typesToBuild.size() > 0);
    }

    static boolean isInConflictWithTaskCreationMethod(String fluidMethodName, Set<String> pBuilderMethodNames) {
        return pBuilderMethodNames.contains(fluidMethodName);
    }

    static boolean isAntTask(Class<?> pDataType) {
        return Task.class.isAssignableFrom(pDataType) && !pDataType.isInterface();
    }

    static boolean isAntType(Class<?> pDataType) {
        return !isAntTask(pDataType) && pDataType.getCanonicalName().startsWith("org.apache.tools.ant") && !pDataType.isInterface();
    }

    private void createDataTypeBuilder(Class<?> pDataType, Set<Class<?>> pDiscoveredTypes, File pDir)
            throws IOException {
        final List<String> applyStatements = new ArrayList<String>();
        final String       builderName     = getBuilderClassName(pDataType);
        final FileWriter   writer          = new FileWriter(new File(pDir, builderName + ".java"));
        writer.write("package " + ELEGANT_TYPE_PACKAGE + ";\n");
        writer.write("import " + ELEGANT_PACKAGE + ".*;\n");
        writer.write("public class " + builderName + " {\n");

        writer.write("private " + ELEGANT_BUILDER + " fBuilder;\n");

        writer.write("public " + builderName + "(" + ELEGANT_BUILDER + " pBuilder){\n");
        writer.write("  fBuilder = pBuilder;\n");
        writer.write("}\n");

        writer.write("public " + Project.class.getName() + " getProject(){return fBuilder.getProject();}\n");

        final List<Method> fileSetters = new ArrayList<Method>();
        new AbstractMethodVisitor() {

            int fIndex;

            @Override
            void visitSetter(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                String fieldName               = getFieldName(pMethod, SET_PREFIX) + fIndex++;
                String elementBuilderClassName = getBuilderClassName(pFirstArgumentType);
                try {
                    writer.write("private " + elementBuilderClassName + " " + fieldName + ";\n");
                    writeNestedElementMethod(builderName, pFluidMethodName, pFirstArgumentType,
                            pMethod.getExceptionTypes(), fieldName + " = pBuilder;\n", writer);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                applyStatements.add(getSurroundingNullCheck(pFirstArgumentType, fieldName, "  pElement."
                        + pMethod.getName() + "(" + fieldName + ".get());\n"));
            }

            @Override
            void visitPlainSetter(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                String fieldName = getFieldName(pMethod, SET_PREFIX) + fIndex++;
                try {
                    writer.write("private " + pFirstArgumentType.getCanonicalName() + fieldName + ";\n");
                    writer.write("public " + builderName + " " + pFluidMethodName + "("
                            + pFirstArgumentType.getCanonicalName() + " pValue){\n");
                    writer.write("  " + fieldName + " = pValue;\n");
                    writer.write("  return this;\n");
                    writer.write("}\n");

                    if (pFirstArgumentType.equals(File.class)) {
                        fileSetters.add(pMethod);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                applyStatements.add(getSurroundingNullCheck(pFirstArgumentType, fieldName, "  pElement."
                        + pMethod.getName() + "(" + fieldName + ");\n"));
            }
            
            @Override
            void visitCreateMethod(Method pMethod, String pFluidMethodName, Class<?> pReturnType) {
                String fieldName               = getFieldName(pMethod, CREATE_PREFIX) + fIndex++;
                String elementBuilderClassName = getBuilderClassName(pReturnType);
                try {
                    generateBuilderListFieldDeclaration(elementBuilderClassName, fieldName, writer);
                    writeNestedElementMethod(builderName, pFluidMethodName, pReturnType, pMethod.getExceptionTypes(),
                            fieldName + ".add(pBuilder);\n", writer);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                String builderStatement = "builder.apply(pElement." + pMethod.getName() + "());\n";
                String loopStatement    = getLoopStatement(elementBuilderClassName, fieldName, builderStatement);
                applyStatements.add(loopStatement);
            }

            @Override
            void visitAddConfiguredMethod(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                String fieldName               = getFieldName(pMethod, ADD_CONFIGURED_PREFIX) + fIndex++;
                String elementBuilderClassName = getBuilderClassName(pFirstArgumentType);
                try {
                    generateBuilderListFieldDeclaration(elementBuilderClassName, fieldName, writer);
                    writeNestedElementMethod(builderName, pFluidMethodName, pFirstArgumentType,
                            pMethod.getExceptionTypes(), fieldName + ".add(pBuilder);\n", writer);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String builderStatement = "pElement." + pMethod.getName() + "(builder.get());\n";
                String loopStatement    = getLoopStatement(elementBuilderClassName, fieldName, builderStatement);
                applyStatements.add(loopStatement);
            }

            @Override
            void visitAddMethod(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
                String fieldName               = getFieldName(pMethod, ADD_PREFIX) + fIndex++;
                String elementBuilderClassName = getBuilderClassName(pFirstArgumentType);
                try {
                    generateBuilderListFieldDeclaration(elementBuilderClassName, fieldName, writer);
                    writeNestedElementMethod(builderName, pFluidMethodName, pFirstArgumentType,
                            pMethod.getExceptionTypes(), fieldName + ".add(pBuilder);\n", writer);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String builderStatement = "pElement." + pMethod.getName() + "(builder.get());\n";
                String loopStatement    = getLoopStatement(elementBuilderClassName, fieldName, builderStatement);
                applyStatements.add(loopStatement);
            }
        }.visit(pDataType, pDiscoveredTypes);

        generateStringToFileSetterMethods(pDataType, builderName, fileSetters, writer);

        String dataTypeClassname = pDataType.getCanonicalName();
        writer.write("public " + dataTypeClassname + " apply(" + dataTypeClassname + " pElement){\n");
        writer.write("    try {\n");
        for (String statement : applyStatements) {
            writer.write(statement);
        }
        writer.write("    return pElement;\n");
        writer.write("    } catch (Exception ex) {\n");
        writer.write("      throw new RuntimeException(ex);\n");
        writer.write("    }\n");
        writer.write("}\n");

        if (hasApplicableNoArgsConstructor(pDataType) || hasApplicableProjectArgConstructor(pDataType)) {
            writer.write("public " + dataTypeClassname + " get(){\n");
            if (hasApplicableNoArgsConstructor(pDataType)) {
                writer.write("    " + dataTypeClassname + " element = new " + dataTypeClassname + "();\n");
                if (DataType.class.isAssignableFrom(pDataType)) {
                    writer.write("    element.setProject(getProject());\n");
                }
                writer.write("    return apply(element);\n");
            } else {
                writer.write("    return apply(new " + dataTypeClassname + "(getProject()));\n");
            }
            writer.write("}\n");
        }
        writer.write("}\n");
        writer.close();
    }

    private void generateStringToFileSetterMethods(Class<?> pDataType, final String pBuilderName,
            final List<Method> pFileSetters, final FileWriter pWriter) throws IOException {
        for (Method method : pFileSetters) {
            if (isCorrespondingStringMethodAlreadyDeclared(pDataType, method)) {
                continue;
            }
            String fluidMethodName = getFluidMethodNameWithoutPrefix(method, AbstractMethodVisitor.SET_PREFIX);
            pWriter.write("public " + pBuilderName + " " + fluidMethodName + "(String pValue)"
                    + getThrowsDeclaration(method.getExceptionTypes()) + "{\n");
            pWriter.write("   return " + fluidMethodName + "(getProject().resolveFile(pValue));\n");
            pWriter.write("}\n");
        }
    }
    
    static void generateBuilderListFieldDeclaration(String pBuilderClassName, String fieldName, final FileWriter writer)
            throws IOException {
        writer.write("private java.util.List<" + pBuilderClassName + "> " + fieldName + " = new java.util.ArrayList<"
                + pBuilderClassName + ">();\n");
    }

    static String getSurroundingNullCheck(Class<?> pType, String pFieldName, String pBodyStatement) {
        if (pType.isPrimitive()) {
            return pBodyStatement;
        }
        return "if(" + pFieldName + "!=null){" + pBodyStatement + "}";
    }

    static String getLoopStatement(final String pBuilderName, String pFieldName, String pBuilderStatement) {
        return "for(" + pBuilderName + " builder : " + pFieldName + "){" + pBuilderStatement + "}\n";
    }

    
    
    //--------------------------------------------------------------------------  
    // utility methods
    //--------------------------------------------------------------------------

    static String getFieldName(Method method, String pPrefix) {
        return " f" + getMethodNameWithoutPrefix(method, pPrefix);
    }

    static String getBuilderClassName(Class<?> pClass) {
        return getBuilderClassPrefix(pClass) + (isAntTask(pClass) ? TASK_BUILDER_SUFFIX : TYPE_BUILDER_SUFFIX);
    }

    static String getBuilderClassPrefix(Class<?> pClass) {
        if (pClass.isMemberClass()) {
            return pClass.getEnclosingClass().getSimpleName() + pClass.getSimpleName();
        }
        return pClass.getSimpleName();
    }


    static String getFluidMethodNameWithoutPrefix(Method pMethod, String pPrefix) {
        String fluidMethodName = StringUtils.uncapitalize(getMethodNameWithoutPrefix(pMethod, pPrefix));
        if (fluidMethodName.length() == 0) {
            return pPrefix;
        }
        return getCompilableMethodName(fluidMethodName);
    }

    static String getCompilableMethodName(String fluidMethodName) {
        if (isJavaKeyword(fluidMethodName)) {
            return "_" + fluidMethodName;
        }
        return fluidMethodName;
    }



    // --------------------------------------------------------------------------
    // Reflection utility methods
    // --------------------------------------------------------------------------

    static boolean isJavaKeyword(String fluidMethodName) {
        return JAVA_KEYWORDS.contains(fluidMethodName);
    }

    static String getMethodNameWithoutPrefix(Method pMethod, String pPrefix) {
        String methodName = pMethod.getName();
        return getStringWithoutPrefix(methodName, pPrefix);
    }

    static String getStringWithoutPrefix(String pString, String pPrefix) {
        return pString.substring(pPrefix.length());
    }

    static String getPathForPackage(String packageName) {
        return packageName.replace('.', '/');
    }


    static boolean hasApplicableProjectArgConstructor(Class<?> pDataType) {
        return (!Modifier.isAbstract(pDataType.getModifiers()) && hasProjectArgConstructor(pDataType));
    }

    static boolean hasProjectArgConstructor(Class<?> pDataType) {
        try {
            return pDataType.getConstructor(new Class<?>[] { Project.class }) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    static boolean hasApplicableNoArgsConstructor(Class<?> pDataType) {
        return (!Modifier.isAbstract(pDataType.getModifiers()) && hasNoArgsConstructor(pDataType));
    }

    static boolean hasNoArgsConstructor(Class<?> pDataType) {
        try {
            return pDataType.getConstructor(new Class<?>[0]) != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
