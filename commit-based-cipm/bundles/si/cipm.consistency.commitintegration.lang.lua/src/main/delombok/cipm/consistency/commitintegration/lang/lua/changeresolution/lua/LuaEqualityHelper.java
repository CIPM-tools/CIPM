package cipm.consistency.commitintegration.lang.lua.changeresolution.lua;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.utils.EqualityHelper;
import org.eclipse.emf.ecore.EObject;
import org.xtext.lua.lua.Block;
import org.xtext.lua.lua.Chunk;
import org.xtext.lua.lua.Component;
import org.xtext.lua.lua.ComponentSet;
import org.xtext.lua.lua.Expression_And;
import org.xtext.lua.lua.Expression_Concatenation;
import org.xtext.lua.lua.Expression_Division;
import org.xtext.lua.lua.Expression_Equal;
import org.xtext.lua.lua.Expression_Exponentiation;
import org.xtext.lua.lua.Expression_False;
import org.xtext.lua.lua.Expression_Functioncall;
import org.xtext.lua.lua.Expression_Functioncall_Direct;
import org.xtext.lua.lua.Expression_Functioncall_Table;
import org.xtext.lua.lua.Expression_Import;
import org.xtext.lua.lua.Expression_Invert;
import org.xtext.lua.lua.Expression_Larger;
import org.xtext.lua.lua.Expression_Larger_Equal;
import org.xtext.lua.lua.Expression_Length;
import org.xtext.lua.lua.Expression_Minus;
import org.xtext.lua.lua.Expression_Modulo;
import org.xtext.lua.lua.Expression_Multiplication;
import org.xtext.lua.lua.Expression_Negate;
import org.xtext.lua.lua.Expression_Nil;
import org.xtext.lua.lua.Expression_Not_Equal;
import org.xtext.lua.lua.Expression_Number;
import org.xtext.lua.lua.Expression_Or;
import org.xtext.lua.lua.Expression_Plus;
import org.xtext.lua.lua.Expression_Smaller;
import org.xtext.lua.lua.Expression_Smaller_Equal;
import org.xtext.lua.lua.Expression_String;
import org.xtext.lua.lua.Expression_TableAccess;
import org.xtext.lua.lua.Expression_TableConstructor;
import org.xtext.lua.lua.Expression_True;
import org.xtext.lua.lua.Expression_VarArgs;
import org.xtext.lua.lua.Expression_VariableName;
import org.xtext.lua.lua.Field_AppendEntryToTable;
import org.xtext.lua.lua.Function;
import org.xtext.lua.lua.Functioncall_Arguments;
import org.xtext.lua.lua.LastStatement_Return;
import org.xtext.lua.lua.LastStatement_Return_WithValue;
import org.xtext.lua.lua.NamedChunk;
import org.xtext.lua.lua.Refble;
import org.xtext.lua.lua.Referenceable;
import org.xtext.lua.lua.Statement_Assignment;
import org.xtext.lua.lua.Statement_For;
import org.xtext.lua.lua.Statement_If_Then_Else;
import org.xtext.lua.lua.Statement_If_Then_Else_ElseIfPart;
import org.xtext.lua.lua.Statement_While;

import com.google.common.cache.LoadingCache;

public class LuaEqualityHelper extends EqualityHelper {

    /**
     * Constructor to initialize the required cache.
     *
     * @param uriCache
     *            The cache to use during the equality checks.
     */
    public LuaEqualityHelper(LoadingCache<EObject, org.eclipse.emf.common.util.URI> uriCache) {
        super(uriCache);
    }

    protected boolean matchEList(EList<? extends EObject> left, EList<? extends EObject> right) {
        if (left == null || right == null) {
            return false;
        }
        if (left.size() != right.size()) {
            return false;
        }
        for (var i = 0; i < left.size(); i++) {
            if (!match(left.get(i), right.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected boolean matchEListByClass(EList<? extends EObject> left, EList<? extends EObject> right) {
        if (left == null || right == null) {
            return false;
        }
        if (left.size() != right.size()) {
            return false;
        }
        for (var i = 0; i < left.size(); i++) {
            if (!eClassMatch(left.get(i), right.get(i))) {
                return false;
            }
        }
        return true;
    }

    protected static boolean eClassMatch(EObject left, EObject right) {
        var leftClass = left.eClass()
            .getName();
        var rightClass = right.eClass()
            .getName();
        return leftClass.equals(rightClass);
    }

    protected static boolean match(Refble left, Refble right) {
        // both null
//        if (left == null && right == null) {
//            return true;
//        }
        // only one null
        if (left == null || right == null) {
            return false;
        }

        return left.getName()
            .equals(right.getName());
    }

//    protected boolean refblesMatchPositionally(Refble left, Refble right) {
//        if (left == null || right == null) {
//            return false;
//        }
//
//        if (!left.getName()
//            .equals(right.getName())) {
//            return false;
//        }
//
//        var leftBlock = EcoreUtil2.getContainerOfType(left, Block.class);
//        var rightBlock = EcoreUtil2.getContainerOfType(right, Block.class);
//        var leftStatement = EcoreUtil2.getContainerOfType(left, Statement.class);
//        var rightStatement = EcoreUtil2.getContainerOfType(right, Statement.class);
//        var leftIndex = leftBlock.getStatements()
//            .indexOf(leftStatement);
//        var rightIndex = rightBlock.getStatements()
//            .indexOf(rightStatement);
//        return leftIndex == rightIndex;
//    }

    protected boolean refblesMatchByScope(Refble left, Refble right) {
        if (left == null || right == null) {
            return false;
        }

        if (!left.getName()
            .equals(right.getName())) {
            return false;
        }

        var lParent = left.eContainer();
        var rParent = right.eContainer();
        while (lParent != null && rParent != null) {
            if (!eClassMatch(lParent, rParent)) {
                return false;
            }
            if (lParent instanceof Statement_Assignment && rParent instanceof Statement_Assignment) {
                var lAss = (Statement_Assignment) lParent;
                var rAss = (Statement_Assignment) rParent;
                if (lAss.getDests()
                    .size() != rAss.getDests()
                        .size()) {
                    return false;
                }
            }
            lParent = lParent.eContainer();
            rParent = rParent.eContainer();
        }

        // if both are now null both scopes have the same depth
        return lParent == null && rParent == null;
    }

    protected boolean match(Expression_VariableName left, Expression_VariableName right) {
        // both null
//        if (left == null && right == null) {
//            return true;
//        }
        // only one null
        if (left == null || right == null) {
            return false;
        }
//        return refblesMatchPositionally(left.getRef(), right.getRef());
        return refblesMatchByScope(left.getRef(), right.getRef());
    }

    protected boolean match(Expression_Functioncall left, Expression_Functioncall right) {
        if (left instanceof Expression_Functioncall_Direct leftCall
                && right instanceof Expression_Functioncall_Direct rightCall) {
            if (leftCall.getCalledFunction() == null || rightCall.getCalledFunction() == null
                    || !refblesMatchByScope(leftCall.getCalledFunction(), rightCall.getCalledFunction())) {
                return false;
            }
        } else if (left instanceof Expression_Functioncall_Table leftCall
                && right instanceof Expression_Functioncall_Table rightCall) {
            if (leftCall.getCalledTable() == null || rightCall.getCalledTable() == null
                    || !refblesMatchByScope(leftCall.getCalledTable(), rightCall.getCalledTable())) {
                return false;
            }
        } else {
            return false;
        }
        return matchEList(left.getCalledFunctionArgs()
            .getArguments(),
                right.getCalledFunctionArgs()
                    .getArguments());
    }

    protected boolean match(Expression_TableConstructor left, Expression_TableConstructor right) {
        return matchEList(left.getFields(), right.getFields());
    }

    protected boolean match(Field_AppendEntryToTable left, Field_AppendEntryToTable right) {
        return match(left.getValue(), right.getValue());
    }

    protected boolean match(Statement_If_Then_Else left, Statement_If_Then_Else right) {
        if (!match(left.getIfExpression(), right.getIfExpression())) {
            return false;
        }
        if (!matchEList(left.getElseIf(), right.getElseIf())) {
            return false;
        }
        if ((left.getElseBlock() != null && right.getElseBlock() == null)
                || (left.getElseBlock() == null && right.getElseBlock() != null)) {
            return false;
        }
        // TODO remove if this does not help:
        // seems to help but not perfectly
        if (!matchEListByClass(left.getBlock().getStatements(), right.getBlock().getStatements())) {
            return false;
        }
//        if (!match(left.getBlock(), right.getBlock())) {
//            return false;
//        }
        return true;
    }

    protected boolean match(Statement_If_Then_Else_ElseIfPart left, Statement_If_Then_Else_ElseIfPart right) {
        // TODO is this enough?
        return match(left.getElseifExpression(), right.getElseifExpression());
    }

    protected boolean match(Statement_For left, Statement_For right) {
        if ((left.isGeneric() != right.isGeneric()) || (left.isNumeric() != right.isNumeric())) {
            return false;
        }
        if (!matchEList(left.getArgExpressions(), right.getArgExpressions())) {
            return false;
        }
        if (!matchEList(left.getArguments(), right.getArguments())) {
            return false;
        }
        return true;
    }

    protected boolean match(Statement_Assignment left, Statement_Assignment right) {
        var leftDests = left.getDests();
        var rightDests = right.getDests();
        
        // TODO remove after debugging
        if (leftDests.size() > 0) {
            if (leftDests.get(0) instanceof Referenceable) {
                var destName = ((Referenceable) leftDests.get(0)).getName();
                var leftParentParent = left.eContainer().eContainer();
                if (destName.equals("counter") && leftParentParent instanceof Statement_If_Then_Else) {
                    var foo = 42;
                }
            }
        }


        if (!matchEList(leftDests, rightDests)) {
            return false;
        }
        // TODO if we uses this to compare the assignment
        // we get a "not contained in a resource error" which indicates
        // that a change in a reference was not detected
//        if (!matchEList(left.getValues(), right.getValues())) {
//            return false;
//        }
        // so im trying this:
//        if (!matchEListByClass(left.getValues(), right.getValues())) {
//            return false;
//        }

        return true;
    }

    protected boolean match(Expression_TableAccess left, Expression_TableAccess right) {
        if ((left.getIndexableExpression() != null || right.getIndexableExpression() != null)
                && !match(left.getIndexableExpression(), right.getIndexableExpression())) {
            return false;
        }

        if (!matchEList(left.getIndexExpression(), right.getIndexExpression())) {
            return false;
        }

        if ((left.getFunctionName() != null && !left.getFunctionName()
            .equals(right.getFunctionName())) || (right.getFunctionName() != null
                    && !right.getFunctionName()
                        .equals(left.getFunctionName()))) {
            return false;
        }

        return true;
    }
    
    /**
     * Do objects match based on lua attributes, like name
     * 
     * @param left
     * @param right
     * @return True if matching, false if not
     */
    public boolean match(EObject left, EObject right) {
        if (left == null || right == null) {
            return false;
        }

        if (!eClassMatch(left, right)) {
            return false;
        }
        
        // this "switch" is ugly, but i have no time to make it nice :/

        // terminals
        if (left instanceof Expression_Nil l && right instanceof Expression_Nil r) {
            return true;
        } else if (left instanceof Expression_True l && right instanceof Expression_True r) {
            return true;
        } else if (left instanceof Expression_False l && right instanceof Expression_False r) {
            return true;
        } else if (left instanceof Expression_VarArgs l && right instanceof Expression_VarArgs r) {
            return true;

            // unary expressions
        } else if (left instanceof Expression_Length l && right instanceof Expression_Length r) {
            return match(l.getExp(), r.getExp());
        } else if (left instanceof Expression_Invert l && right instanceof Expression_Invert r) {
            return match(l.getExp(), r.getExp());
        } else if (left instanceof Expression_Negate l && right instanceof Expression_Negate r) {
            return match(l.getExp(), r.getExp());
        } else if (left instanceof Expression_Number l && right instanceof Expression_Number r) {
            return l.getValue() == r.getValue();
        } else if (left instanceof Expression_String l && right instanceof Expression_String r) {
            return l.getValue()
                .equals(r.getValue());

            // binary expressions
        } else if (left instanceof Expression_Or l && right instanceof Expression_Or r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_And l && right instanceof Expression_And r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Larger l && right instanceof Expression_Larger r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Larger_Equal l && right instanceof Expression_Larger_Equal r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Smaller l && right instanceof Expression_Smaller r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Smaller_Equal l && right instanceof Expression_Smaller_Equal r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Equal l && right instanceof Expression_Equal r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Not_Equal l && right instanceof Expression_Not_Equal r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Concatenation l && right instanceof Expression_Concatenation r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Plus l && right instanceof Expression_Plus r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Minus l && right instanceof Expression_Minus r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Multiplication l && right instanceof Expression_Multiplication r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Division l && right instanceof Expression_Division r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Exponentiation l && right instanceof Expression_Exponentiation r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());
        } else if (left instanceof Expression_Modulo l && right instanceof Expression_Modulo r) {
            return match(l.getLeft(), r.getLeft()) && match(l.getRight(), r.getRight());

            // other expressions
        } else if (left instanceof Expression_TableAccess l && right instanceof Expression_TableAccess r) {
            return match(l, r);
        } else if (left instanceof Expression_TableConstructor l && right instanceof Expression_TableConstructor r) {
            return match(l, r);
        } else if (left instanceof Expression_Functioncall l && right instanceof Expression_Functioncall r) {
            return match(l, r);
        } else if (left instanceof Expression_VariableName l && right instanceof Expression_VariableName r) {
            return match(l, r);
        } else if (left instanceof Expression_Import l && right instanceof Expression_Import r) {
            return l.getImportURI().equals(r.getImportURI());

            // remaining statements etc.
        } else if (left instanceof Statement_Assignment l && right instanceof Statement_Assignment r) {
            return match(l, r);
        } else if (left instanceof Statement_If_Then_Else l && right instanceof Statement_If_Then_Else r) {
            return match(l, r);
        } else if (left instanceof Statement_If_Then_Else_ElseIfPart l
                && right instanceof Statement_If_Then_Else_ElseIfPart r) {
            return match(l, r);
        } else if (left instanceof Statement_While l && right instanceof Statement_While r) {
            return match(l.getExpression(), r.getExpression());
        } else if (left instanceof Statement_For l && right instanceof Statement_For r) {
            return match(l, r);
        } else if (left instanceof Field_AppendEntryToTable l && right instanceof Field_AppendEntryToTable r) {
            return match(l, r);
        } else if (left instanceof Functioncall_Arguments l && right instanceof Functioncall_Arguments r) {
            return matchEList(l.getArguments(), r.getArguments());
        } else if (left instanceof Function l && right instanceof Function r) {
            return matchEList(l.getArguments(), r.getArguments());
        } else if (left instanceof LastStatement_Return_WithValue l && right instanceof LastStatement_Return_WithValue r) {
            return matchEList(l.getReturnValues(), r.getReturnValues());
        } else if (left instanceof Refble l && right instanceof Refble r) {
            return match(l, r);
            
        // meta model stuff
        // we say these match because of the hierarchical matching taking place
        } else if (left instanceof Block l && right instanceof Block r) {
            return true;
        } else if (left instanceof Chunk l && right instanceof Chunk r) {
            return true;
        } else if (left instanceof ComponentSet l && right instanceof ComponentSet r) {
            return true;
        } else if (left instanceof LastStatement_Return l && right instanceof LastStatement_Return r) {
            return true;
          
        // these have names:
        } else if (left instanceof NamedChunk l && right instanceof NamedChunk r) {
            return l.getName().equals(r.getName());
        } else if (left instanceof Component l && right instanceof Component r) {
            return l.getName().equals(r.getName());
        }

        // no decision from the custom matchers -> use fallback
        // TODO reenable
//        return editionDistanceEqualityChecker.match(left, right);
        return false;
    }

    @Override
    protected boolean matchingEObjects(EObject left, EObject right) {
        return match(left, right);
    }
}
