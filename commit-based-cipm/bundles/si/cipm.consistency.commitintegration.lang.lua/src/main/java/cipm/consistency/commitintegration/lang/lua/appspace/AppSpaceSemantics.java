package cipm.consistency.commitintegration.lang.lua.appspace;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.xtext.lua.LuaUtil;
import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Statement_Function_Declaration;

public class AppSpaceSemantics {

    private static final String SERVE_FUNCTION_NAME = "Script.serveFunction";
    private static final int SERVE_FUNCTION_ARG_COUNT = 2;
    private static final String REGISTER_FUNCTION_SUFFIX = ".register";
    private static final int REGISTER_FUNCTION_ARG_MIN = 2;
    private static final int REGISTER_FUNCTION_ARG_MAX = 3;

    public static boolean isServingFunctionCall(Expression_Functioncall_Direct call) {
        if (call == null) {
            return false;   
        }

        var calledFunction = call.getCalledFunction();
        if (calledFunction == null) {
            return false;
        }

        var functionName = calledFunction.getName();
        var argumentCount = call.getCalledFunctionArgs()
            .getArguments()
            .size();

        return (functionName.equals(SERVE_FUNCTION_NAME) && argumentCount == SERVE_FUNCTION_ARG_COUNT)
                || (functionName.endsWith(REGISTER_FUNCTION_SUFFIX) && REGISTER_FUNCTION_ARG_MIN <= argumentCount
                        && argumentCount <= REGISTER_FUNCTION_ARG_MAX);
    }

    public static Statement_Function_Declaration getFunctionDeclarationFromServingFunctionCall(
            Expression_Functioncall_Direct call) {
        var arguments = call.getCalledFunctionArgs()
            .getArguments();

        if (isServingFunctionCall(call)) {
            return resolveServeArgToDeclaration(arguments.get(arguments.size() - 1));
        }

        return null;
    }

    private static Statement_Function_Declaration resolveServeArgToDeclaration(EObject eObj) {
        if (eObj instanceof Expression_VariableName var
                && var.getRef() instanceof Statement_Function_Declaration decl) {
            return decl;
        } else if (eObj instanceof Expression_String stringExp) {
            var chunk = EcoreUtil2.getContainerOfType(eObj, Chunk.class);
            var declName = LuaUtil.expressionStringToString(stringExp);
            var decl = LuaUtil.getFunctionDeclarationByName(declName, chunk);
            if (decl.isPresent()) {
                return decl.get();
            }
        }
        return null;
    }
}
