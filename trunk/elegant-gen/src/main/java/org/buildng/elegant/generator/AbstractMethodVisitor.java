package org.buildng.elegant.generator;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Task;


/**
 * @see IntrospectionHelper for design rationale.
 * @author thomas
 */
abstract class AbstractMethodVisitor {

    // --------------------------------------------------------------------------
    // constants
    // --------------------------------------------------------------------------

    static final String ADD_TEXT              = "addText";
    static final String ADD_PREFIX            = "add";
    static final String ADD_CONFIGURED_PREFIX = "addConfigured";
    static final String CREATE_PREFIX         = "create";
    static final String SET_PREFIX            = "set";



    // --------------------------------------------------------------------------
    // instance methods
    // --------------------------------------------------------------------------

    public void visit(Class<?> pElementClass, Set<Class<?>> pDiscoveredTypes) {
        Method[] methods = pElementClass.getMethods();
        for (Method method : methods) {
            String   methodName        = method.getName();
            Class<?> returnType        = method.getReturnType();
            int      numParameters     = method.getParameterTypes().length;
            Class<?> firstArgumentType = numParameters > 0 ? method.getParameterTypes()[0] : null;

            if (methodName.equals(ADD_TEXT)) {
                visitAddText();
            } else if (methodName.startsWith(SET_PREFIX) && java.lang.Void.TYPE.equals(returnType)
                    && numParameters == 1 && !firstArgumentType.isArray()) {

                String fluidMethodName = FluidGenerator.getFluidMethodNameWithoutPrefix(method, SET_PREFIX);
                if (needsABuilder(firstArgumentType)) {
                    visitSetter(method, fluidMethodName, firstArgumentType);

                    pDiscoveredTypes.add(firstArgumentType);
                } else {
                    visitPlainSetter(method, fluidMethodName, firstArgumentType);
                }
            } else if (methodName.startsWith(CREATE_PREFIX) && !returnType.isArray() && !returnType.isPrimitive()
                    && numParameters == 0) {

                if (isAntTaskOrType(returnType)) {
                    String fluidMethodName = getAddMethodName(FluidGenerator.getFluidMethodNameWithoutPrefix(method,
                                                     CREATE_PREFIX));
                    visitCreateMethod(method, fluidMethodName, returnType);

                    pDiscoveredTypes.add(returnType);
                } else {
                    visitOtherNestedElementMethod(method);
                }
            } else if (methodName.startsWith(ADD_CONFIGURED_PREFIX)
                    && FluidGenerator.isFluidAddMethod(firstArgumentType, returnType, numParameters)) {
                if (needsABuilder(firstArgumentType) && hasApplicableConstructor(firstArgumentType)) {
                    String fluidMethodName = getAddMethodName(FluidGenerator.getFluidMethodNameWithoutPrefix(method,
                                                     ADD_CONFIGURED_PREFIX));
                    visitAddConfiguredMethod(method, fluidMethodName, firstArgumentType);

                    pDiscoveredTypes.add(firstArgumentType);
                } else {
                    visitOtherNestedElementMethod(method);
                }
            } else if (methodName.startsWith(ADD_PREFIX)
                    && FluidGenerator.isFluidAddMethod(firstArgumentType, returnType, numParameters)
                    && FluidGenerator.hasNoCorrespondingAddConfiguredMethod(pElementClass, methodName,
                            firstArgumentType)) {

                if (needsABuilder(firstArgumentType) && hasApplicableConstructor(firstArgumentType)) {
                    String fluidMethodName = method.getName();
                    visitAddMethod(method, fluidMethodName, firstArgumentType);

                    pDiscoveredTypes.add(firstArgumentType);
                } else {
                    visitOtherNestedElementMethod(method);
                }
            } else {
                visitOtherMethod(method);
            }
        }
    }

    static boolean needsABuilder(Class<?> pDataType) {
      return isAntTaskOrType(pDataType) && hasApplicableConstructor(pDataType);
  }

    static boolean isAntTaskOrType(Class<?> pDataType) {
        return (isAntTask(pDataType) || isAntType(pDataType));
    }

    static boolean isAntType(Class<?> pDataType) {
        return !isAntTask(pDataType) && pDataType.getCanonicalName().startsWith("org.apache.tools.ant") && !pDataType.isInterface();
    }

    static boolean isAntTask(Class<?> pDataType) {
        return Task.class.isAssignableFrom(pDataType) && !pDataType.isInterface();
    }
    
    static boolean hasApplicableConstructor(Class<?> pDataType) {
        return FluidGenerator.hasApplicableNoArgsConstructor(pDataType)
                || FluidGenerator.hasApplicableProjectArgConstructor(pDataType);
    }

    private String getAddMethodName(String methodName) {
        return ADD_PREFIX + StringUtils.capitalize(methodName);
    }

    void visitAddText() {
        // intentionally left blank
    }

    void visitSetter(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
        // intentionally left blank
    }

    void visitPlainSetter(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
        // intentionally left blank
    }

    void visitCreateMethod(Method pMethod, String pFluidMethodName, Class<?> pReturnType) {
        // intentionally left blank
    }

    void visitAddConfiguredMethod(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
        // intentionally left blank
    }

    void visitAddMethod(Method pMethod, String pFluidMethodName, Class<?> pFirstArgumentType) {
        // intentionally left blank
    }

    void visitOtherMethod(Method pMethod) {
        // intentionally left blank
    }

    private void visitOtherNestedElementMethod(Method pMethod) {
        // intentionally left blank
    }
}
