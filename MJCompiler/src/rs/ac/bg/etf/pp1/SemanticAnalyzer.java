package rs.ac.bg.etf.pp1;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticAnalyzer extends VisitorAdaptor {

	// brojaci za izvestaj sintaksne analize
	public int printCallCount = 0;
	public int varDeclCount = 0;
	public int globalVarDeclCount = 0;
	public int localVarDeclCount = 0;
	
	Obj currentType;
	Obj currentMethod = null;
	String currentMethodName = "";
	boolean returnFound = false;
	boolean errorDetected = false;

	//bool tip
	Obj boolType;
	
	// globalna oblast vazenja
	Scope programScope;

	// lista svih definisanih metoda programa organizovana po imenima i sadrzi skup
	// svih formalnih parametara pojedinacnog metoda
	HashMap<String, LinkedList<Obj>> allMethodDeclarations = new HashMap<String, LinkedList<Obj>>();
	
	// lista za skupljanje formalnih parametara u toku semanticke analize
	LinkedList<Obj> currentFormals = new LinkedList<Obj>();
	
	// lista za skupljanje stvarnih parametara u toku semanticke analize
	LinkedList<Obj> actualParams = new LinkedList<Obj>();
	
	// da li se trenutno nalazimo u do-while petlji i u kom nivou
	int doWhile = 0;

	Logger log = Logger.getLogger(getClass());

	public SemanticAnalyzer() {
		Tab.init();
		boolType = new Obj(Obj.Type, "bool", new Struct(Struct.Bool));
		Tab.currentScope.addToLocals(boolType);

		// ********* ord *********//
		currentFormals = new LinkedList<Obj>();
		currentFormals.add(new Obj(Obj.Var, "", Tab.charType));
		allMethodDeclarations.put("ord", currentFormals);

		// ********* chr *********//
		currentFormals = new LinkedList<Obj>();
		currentFormals.add(new Obj(Obj.Var, "", Tab.intType));
		allMethodDeclarations.put("chr", currentFormals);

		// ********* len *********//
		Struct arrayType = new Struct(Struct.Array);
		arrayType.setElementType(Tab.noType);
		currentFormals = new LinkedList<Obj>();
		currentFormals.add(new Obj(Obj.Var, "", arrayType));
		allMethodDeclarations.put("len", currentFormals);

		currentFormals = new LinkedList<Obj>();
	}

	// ===================POMOCNE METODE ZA IZVESTAVANJE=======================//

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

	public boolean passed() {
		return !errorDetected;
	}

	// =============================POMOCNE METODE================================//

	private boolean correctFunctionCall(LinkedList<Obj> formalParams) {

		int actualsNumber = actualParams.size() - 1;
		int formsNumber = formalParams.size() - 1;
		int d = formsNumber - actualsNumber;

		// provera tipova formalnih i stvarnih argumenata
		for (int i = formsNumber; i >= d; i--) {
			Obj actual = actualParams.get(i - d);
			Obj formal = formalParams.get(i);

			if (!actual.getType().assignableTo(formal.getType()))
				return false;
		}

		// provera da li funkcija podrzava dati broj stvarnih argumenata
		if (formalParams.size() > actualParams.size()) {
			if (formalParams.get(d - 1).getKind() != Obj.Con)
				return false;
		}
		return true;
	}

	public HashMap<String, LinkedList<Obj>> getAllMethodDeclarations() {
		return allMethodDeclarations;
	}

	// ==================================PROGRAM========================================//

	public void visit(ProgName progName) {
		progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
		Tab.openScope();
		programScope = Tab.currentScope();
	}

	public void visit(Program program) {
		// nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
	}

	// ================================PROMENLJIVE====================================//

	public void visit(VarName varName) {
		Obj variableName = Tab.find(varName.getVariableName());
		if (variableName != Tab.noObj) {
			report_error("Ime " + variableName.getName() + " je vec deklarisano ", varName);
			return;
		}

		if (currentType.getType() != Tab.noType) {
			report_info("Deklarisana promenljiva " + varName.getVariableName(), varName);
			Obj varNode = Tab.insert(Obj.Var, varName.getVariableName(), currentType.getType());
			if (Tab.currentScope().equals(programScope)) {
				globalVarDeclCount++;
				varNode.setLevel(0);
			} else {
				localVarDeclCount++;
				varNode.setLevel(1);
			}
			varDeclCount++;
		}
	}

	public void visit(ArrayName arrName) {
		Obj variableName = Tab.find(arrName.getArrayName());
		if (variableName != Tab.noObj) {
			report_error("Ime " + variableName.getName() + " je vec deklarisano!", null);
		}
		Struct arrayType = new Struct(Struct.Array, currentType.getType());

		if (currentType.getType() != Tab.noType) {
			report_info("Deklarisana promenljiva " + arrName.getArrayName(), arrName);
			Obj varNode = Tab.insert(Obj.Var, arrName.getArrayName(), arrayType);
			if (Tab.currentScope().equals(programScope)) {
				globalVarDeclCount++;
				varNode.setLevel(0);
			} else {
				localVarDeclCount++;
				varNode.setLevel(1);
			}
			varDeclCount++;
		}
	}

	public void visit(VarDeclaration varDeclaration) {
		currentType = null;
	}

	// ========================================TIP============================================//

	public void visit(Type type) {
		currentType = Tab.find(type.getTypeName());
		if (currentType == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola! ", null);
			type.struct = Tab.noType;
		} else {
			if (Obj.Type == currentType.getKind()) {
				type.struct = currentType.getType();
			} else {
				report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip!", type);
				type.struct = Tab.noType;
			}
		}
	}

	// =======================================KONSTANTA=======================================//

	public void visit(ConstDeclSingle constDeclSingle) {
		ConstVal constVal = constDeclSingle.getConstVal();
		String name = constDeclSingle.getConstName();

		if (constVal instanceof NumConstant) {
			if (currentType.getType().getKind() != Struct.Int) {
				report_error("Greska: Tip i vrednost konstante " + name + " nisu kompatibilni!", constDeclSingle);
				return;
			}

			NumConstant numConstant = (NumConstant) constVal;
			int value = numConstant.getNumConst().getNumVal();

			report_info("Definisana konstanta " + name, constDeclSingle);
			Obj varNode = Tab.insert(Obj.Con, name, currentType.getType());
			varNode.setAdr(value);
			varNode.setLevel(0);
			constDeclSingle.obj = varNode;
		} else if (constVal instanceof CharConstant) {
			if (currentType.getType().getKind() != Struct.Char) {
				report_error("Greska: Tip i vrednost konstante " + name + " nisu kompatibilni!", constDeclSingle);
				return;
			}

			CharConstant charConstant = (CharConstant) constVal;
			char value = charConstant.getCharConst().getCharVal();

			report_info("Definisana konstanta " + name, constDeclSingle);
			Obj varNode = Tab.insert(Obj.Con, name, currentType.getType());
			varNode.setAdr(value);
			varNode.setLevel(0);
			constDeclSingle.obj = varNode;
		} else if (constVal instanceof BoolConstant) {
			if (currentType.getType().getKind() != Struct.Bool) {
				report_error("Greska: Tip i vrednost konstante " + name + " nisu kompatibilni!", constDeclSingle);
				return;
			}

			BoolConstant boolConstant = (BoolConstant) constVal;
			boolean value = boolConstant.getBoolConst().getBoolVal();

			report_info("Definisana konstanta " + name, constDeclSingle);
			Obj varNode = Tab.insert(Obj.Con, name, currentType.getType());
			varNode.setAdr(value ? 1 : 0);
			varNode.setLevel(0);
			constDeclSingle.obj = varNode;
		}
	}

	public void visit(ConstDeclaration constDeclaration) {
		currentType = null;
	}

	// =======================================METODA==========================================//

	public void visit(TypeMethodName typeMethodName) {
		String name = typeMethodName.getMethName();
		currentMethodName = name;
		currentMethod = Tab.insert(Obj.Meth, name, currentType.getType());
		typeMethodName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + name, typeMethodName);
	}

	public void visit(VoidMethodName voidMethodName) {
		String name = voidMethodName.getMethName();
		currentMethodName = name;
		currentMethod = Tab.insert(Obj.Meth, name, Tab.noType);
		voidMethodName.obj = currentMethod;
		Tab.openScope();
		report_info("Obradjuje se funkcija " + name, voidMethodName);
	}

	public void visit(MethodDecl methodDecl) {
		if (!returnFound && currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funkcija " + currentMethod.getName()
					+ " nema return iskaz!", null);
		}
		currentMethod.setLevel(currentFormals.size());
		allMethodDeclarations.put(currentMethodName, currentFormals);
		currentFormals = new LinkedList<>();
		currentMethodName = "";
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();

		returnFound = false;
		currentType = null;
		currentMethod = null;
	}

	// =======================FORMALNI PARAMETRI METODE===========================//

	public void visit(MethodArgVariable methodArgVariable) {
		if (currentType.getType() != Tab.noType) {
			report_info("Deklarisan formalni argument " + methodArgVariable.getFormalVarName(), methodArgVariable);
			Obj varNode = Tab.insert(Obj.Var, methodArgVariable.getFormalVarName(), currentType.getType());
			currentFormals.push(varNode);
		}
	}

	public void visit(MethodArgArray methodArgArray) {
		String name = methodArgArray.getFormalArrName();

		Struct arrayType = new Struct(Struct.Array, currentType.getType());

		report_info("Deklarisan formalni argument " + name, methodArgArray);
		Obj varNode = Tab.insert(Obj.Fld, name, arrayType);
		currentFormals.push(varNode);
	}

	// =============================OPCIONI ARGUMENTI============================//
	public void visit(OptArg optArg) {
		ConstVal constVal = optArg.getConstVal();
		int constType = currentType.getType().getKind();
		String name = optArg.getOptArgName();

		if (constVal instanceof NumConstant) {
			if (constType != Struct.Int) {
				report_error("Tip i vrednost opcionog argumenta " + name + " nisu kompatibilni", optArg);
				return;
			}

			NumConstant numConstant = (NumConstant) constVal;
			int value = numConstant.getNumConst().getNumVal();

			report_info("Definisan opcioni argument " + name, optArg);
			Obj varNode = new Obj(Obj.Con, name, currentType.getType());
			Obj tabVar = Tab.insert(Obj.Var, name, currentType.getType());
			tabVar.setLevel(1);
			varNode.setAdr(value);
			varNode.setLevel(1);
			currentFormals.push(varNode);
		} else if (constVal instanceof CharConstant) {
			if (constType != Struct.Char) {
				report_error("Tip i vrednost opcionog argumenta " + name + " nisu kompatibilni", optArg);
				return;
			}

			CharConstant charConstant = (CharConstant) constVal;
			char value = charConstant.getCharConst().getCharVal();

			report_info("Definisan opcioni argument " + name, optArg);
			Obj varNode = new Obj(Obj.Con, name, currentType.getType());
			Obj tabVar = Tab.insert(Obj.Var, name, currentType.getType());
			tabVar.setLevel(1);
			varNode.setAdr(value);
			varNode.setLevel(1);
			currentFormals.push(varNode);
		} else if (constVal instanceof BoolConstant) {
			if (constType != Struct.Bool) {
				report_error("Tip i vrednost opcionog argumenta " + name + " nisu kompatibilni", optArg);
				return;
			}

			BoolConstant boolConstant = (BoolConstant) constVal;
			boolean value = boolConstant.getBoolConst().getBoolVal();

			report_info("Definisan opcioni argument " + name, optArg);
			Obj varNode = new Obj(Obj.Con, name, currentType.getType());
			Obj tabVar = Tab.insert(Obj.Var, name, currentType.getType());
			tabVar.setLevel(1);
			varNode.setAdr(value ? 1 : 0);
			varNode.setLevel(1);
			currentFormals.push(varNode);
		}
	}

	// ==========================PROMENLJIVA U IZRAZU============================//

	/*
	 * public void visit(DesignatorStmnt designatorStmnt) {
	 * report_info("Obradjuje se iskaz", designatorStmnt); }
	 */

	public void visit(DesignatorAssign designatorAssign) {
		Obj designator = designatorAssign.getDesignator().obj;
		designatorAssign.getDesignator().setParent(designatorAssign);
		if (designator == Tab.noObj || designator == null) {
			return;
		}

		if (designator.getKind() != Obj.Var && designator.getKind() != Obj.Fld && designator.getKind() != Obj.Elem) {
			report_error("Greska na liniji " + designatorAssign.getLine() + ": Ime " + "" + designator.getName()
					+ " ne predstavlja promenljivu", null);
		}

		if (designator.getType().getKind() == Struct.Array
				&& designatorAssign.getExpr().obj.getType().getKind() == Struct.Array) {
			if (!designator.getType().getElemType()
					.compatibleWith(designatorAssign.getExpr().obj.getType().getElemType())) {
				report_error("Greska na liniji " + designatorAssign.getLine()
						+ ": Nije moguce izvrsiti dodelu vrednosti" + " kod nekompatibilnih tipova.", null);
				return;
			}
		} else if (!designator.getType().compatibleWith(designatorAssign.getExpr().obj.getType())) {
			report_error("Greska na liniji " + designatorAssign.getLine() + ": Nije moguce izvrsiti dodelu vrednosti"
					+ " kod nekompatibilnih tipova.", null);
		}

		report_info("Obradjuje se dodela vrednosti", designatorAssign);

	}

	public void visit(DesignatorIncrement designatorIncrement) {
		Obj designator = designatorIncrement.getDesignator().obj;
		if (designator == Tab.noObj) {
			return;
		}

		if (designator.getType() != Tab.intType) {
			report_error(
					"Greska na liniji " + designatorIncrement.getLine() + ": Nije "
							+ "moguce izvrsiti operaciju ++. Promenljiva " + designator.getName() + " nije tipa int.",
					null);
		}

		report_info("Obradjuje se inkrementiranje", designatorIncrement);
	}

	public void visit(DesignatorDecrement designatorDecrement) {
		Obj designator = designatorDecrement.getDesignator().obj;
		if (designator == Tab.noObj) {
			return;
		}

		if (designator.getType() != Tab.intType) {
			report_error(
					"Greska na liniji " + designatorDecrement.getLine() + ": Nije "
							+ "moguce izvrsiti operaciju ++. Promenljiva " + designator.getName() + " nije tipa int.",
					null);
		}
		report_info("Obradjuje se dekrementiranje", designatorDecrement);
	}

	public void visit(DesignatorName designatorName) {
		designatorName.obj = Tab.find(designatorName.getDesignatorName());
	}

	public void visit(Designator designator) {
		Obj obj = Tab.find(designator.getDesignatorName().getDesignatorName());
		if (obj == Tab.noObj) {
			report_error("Greska na liniji " + designator.getLine() + " : Ime " + designator.getDesignatorName().getDesignatorName()
					+ " nije deklarisano! ", null);
			designator.obj = Tab.noObj;
			return;
		}

		if (designator.getDesignators() instanceof NonDesignators)
			designator.obj = obj;
		else {

			// pokupi ceo niz operatora [] ili . koji se koriste u istom pristupanju
			LinkedList<DesignatorElem> desElemList = new LinkedList<DesignatorElem>();
			Designators designators = designator.getDesignators();
			while (designators instanceof DesignatorList) {
				DesignatorList l = (DesignatorList) designators;
				desElemList.push(l.getDesignatorElem());
				designators = l.getDesignators();
			}

			Obj errorDetector = obj;

			// za nivo 2 dozvoljeni su samo nizovi
			if (desElemList.size() > 1) {
				report_error("Greska na liniji " + designator.getLine() + " : Neispravno upotrebljavanje"
						+ " operatora '.' ili '[]'.", null);
				return;
			}

			DesignatorElem desElem = desElemList.pop();
			if (desElem instanceof ExprDesignatorElem && errorDetector.getType().getKind() != Struct.Array) {
				report_error("Greska na liniji " + designator.getLine() + " : Neispravno upotrebljavanje"
						+ " operatora '[]'.", null);
			}

			designator.obj = new Obj(Obj.Elem, "arrayElem", errorDetector.getType().getElemType());
		}
	}

	// =======================================FAKTOR=========================================//

	public void visit(NumConst numConst) {
		currentType = new Obj(Obj.Var, "", Tab.intType);
	}

	public void visit(CharConst charConst) {
		currentType = new Obj(Obj.Var, "", Tab.charType);
	}

	public void visit(BoolConst boolConst) {
		currentType = new Obj(Obj.Var, "", boolType.getType());
	}

	public void visit(NumFactor numFactor) {
		numFactor.obj = currentType;
	}

	public void visit(CharFactor charFactor) {
		charFactor.obj = currentType;
	}

	public void visit(BoolFactor boolFactor) {
		boolFactor.obj = currentType;
	}

	public void visit(NewFactor newFactor) {
		newFactor.obj = currentType;
	}

	public void visit(NewExprFactor factor) {
		Obj obj = factor.getExpr().obj;
		if (obj.getType() != Tab.intType) {
			report_error("Greska na liniji " + factor.getLine() + " : Tip izraza koji se koristi za indeksiranje"
					+ " mora biti int.", null);
		}
		Struct type = factor.getType().struct;
		factor.obj = new Obj(obj.getKind(), "", new Struct(Struct.Array, type));
	}

	public void visit(DesignatorNonParamsFactor factor) {
		Designator designator = factor.getDesignator();
		if (designator.obj.getKind() != Obj.Meth) {
			report_error("Greska na liniji " + factor.getLine() + " : Ime " + designator.getDesignatorName()
					+ " ne predstavlja ime metode.", null);
		} else
			report_info("Poziv funkcije bez argumenata " + designator.obj.getName(), designator);
		factor.obj = designator.obj;
	}

	public void visit(DesignatorParamsFactor factor) {
		Designator designator = factor.getDesignator();
		boolean error = false;
		if (designator.obj.getKind() != Obj.Meth) {
			report_error("Greska na liniji " + factor.getLine() + " : Ime " + designator.getDesignatorName()
					+ " ne predstavlja ime metode.", null);
			error = true;
		}

		LinkedList<Obj> formalParams = allMethodDeclarations.get(designator.obj.getName());

		if (formalParams.size() < actualParams.size()) {
			report_error("Greska na liniji " + factor.getLine() + " : Previse argumenata za funkciju "
					+ designator.obj.getName(), null);
			error = true;
		} else {

			if (!correctFunctionCall(formalParams)) {
				report_error("Greska na liniji " + factor.getLine() + " : Tipovi stvarnih argumenata funkcije "
						+ designator.obj.getName() + " se ne slazu sa njenim potpisom.", null);
				error = true;
			}
		}

		if (!error)
			report_info("Poziv funkcije sa datim argumentima " + designator.obj.getName(), designator);

		actualParams = new LinkedList<Obj>();
		factor.obj = designator.obj;
	}

	public void visit(DesignatorFactor designatorFactor) {
		designatorFactor.obj = designatorFactor.getDesignator().obj;
	}

	public void visit(ExprFactor factor) {
		factor.obj = factor.getExpr().obj;
	}

	public void visit(ActualParameter actualParameter) {
		actualParams.push(actualParameter.getExpr().obj);
		actualParameter.obj = actualParameter.getExpr().obj;
	}

	public void visit(ActualParameters actualParameters) {
		actualParams.push(actualParameters.getExpr().obj);
	}

	// =========================================TERM=========================================//

	public void visit(FactorList factorList) {
		if (factorList.getFactors().obj.getType() != Tab.intType) {
			report_error("Greska na liniji " + factorList.getLine() + " : Operacija je dozvoljena samo za "
					+ "promenljive koje su tipa int. Problem je " + factorList.getFactor().obj.getName(), null);
		}
		if (factorList.getFactor().obj.getType() != Tab.intType) {
			report_error("Greska na liniji " + factorList.getLine() + " : Operacija je dozvoljena samo za "
					+ "promenljive koje su tipa int. Problem je " + factorList.getFactor().obj.getName(), null);
		}
		factorList.obj = factorList.getFactor().obj;
	}

	public void visit(SingleFactor singleFactor) {
		singleFactor.obj = singleFactor.getFactor().obj;
	}

	public void visit(Term term) {
		term.obj = term.getFactors().obj;
	}

	// =======================================EXPRESSION=========================================//

	public void visit(SingleNegativeTerm singleNegativeTerm) {
		if (singleNegativeTerm.getTerm().obj.getType() != Tab.intType) {
			report_error("Greska na liniji " + singleNegativeTerm.getLine() + " : Operand '-' nije "
					+ "dozvoljen kod tipova koji nisu int.", null);
		}
		singleNegativeTerm.obj = singleNegativeTerm.getTerm().obj;
	}

	public void visit(SinglePositiveTerm singlePositiveTerm) {
		singlePositiveTerm.obj = singlePositiveTerm.getTerm().obj;
	}

	public void visit(MultipleTermExpr multipleTermExpr) {
		if (multipleTermExpr.getTermExpr().obj.getType() != Tab.intType) {
			report_error(
					"Greska na liniji " + multipleTermExpr.getLine() + " : Operacija je dozvoljena samo za "
							+ "promenljive koje su tipa int. Problem je " + multipleTermExpr.getTerm().obj.getName(),
					null);
		}
		if (multipleTermExpr.getTerm().obj.getType() != Tab.intType) {
			report_error(
					"Greska na liniji " + multipleTermExpr.getLine() + " : Operacija je dozvoljena samo za "
							+ "promenljive koje su tipa int. Problem je " + multipleTermExpr.getTerm().obj.getName(),
					null);
		}
		multipleTermExpr.obj = multipleTermExpr.getTerm().obj;
	}

	public void visit(SingleExpr singleExpr) {
		singleExpr.obj = singleExpr.getTermExpr().obj;
	}

	public void visit(SingleExpression singleExpression) {
		singleExpression.obj = singleExpression.getSingleExpr().obj;
	}

	public void visit(NullishOperator nullishOperator) {
		if (nullishOperator.getExpr().obj.getType() != Tab.intType) {
			report_error("Greska na liniji " + nullishOperator.getLine() + " : Operacija '??' je dozvoljena samo za "
					+ "promenljive koje su tipa int.", null);
		}
		if (nullishOperator.getSingleExpr().obj.getType() != Tab.intType) {
			report_error("Greska na liniji " + nullishOperator.getLine() + " : Operacija '??' je dozvoljena samo za "
					+ "promenljive koje su tipa int.", null);
		}
		nullishOperator.obj=nullishOperator.getExpr().obj;
	}

	// =======================================CONDITION=========================================//

	public void visit(ExprFact exprFact) {
		exprFact.obj = exprFact.getExpr().obj;
	}

	public void visit(RelopFact relopFact) {
		Obj first = relopFact.getExpr().obj;
		Obj second = relopFact.getExpr1().obj;
		Relop relop = relopFact.getRelop();
		if (!first.getType().compatibleWith(second.getType())) {
			report_error("Greska na liniji " + relopFact.getLine() + " : Tipovi izraza nisu kompatibilni.", null);
		}

		if (first.getType().getKind() == Struct.Array && !(relop instanceof Same) && !(relop instanceof Differ)) {
			report_error("Greska na liniji " + relopFact.getLine() + " : Nije dozvoljeno korizcenje operatora "
					+ " >, >=, <, <= kod nizova.", null);
		}

		relopFact.obj = new Obj(Obj.Var, "", boolType.getType());
	}

	public void visit(ConditionFact conditionFact) {
		conditionFact.obj = conditionFact.getCondFact().obj;
	}

	public void visit(ConditionFacts conditionFacts) {
		if (conditionFacts.getCondFact().obj.getType() != boolType.getType()) {
			report_error("Greska na liniji " + conditionFacts.getLine() + " : Operator '&&' "
					+ "se koristi samo za iskaze tipa bool.", null);
		}
		if (conditionFacts.getCondTerm().obj.getType() != boolType.getType()) {
			report_error("Greska na liniji " + conditionFacts.getLine() + " : Operator '&&' "
					+ "se koristi samo za iskaze tipa bool.", null);
		}
		conditionFacts.obj = conditionFacts.getCondTerm().obj;
	}

	public void visit(ConditionTerm conditionTerm) {
		conditionTerm.obj = conditionTerm.getCondTerm().obj;
		if (!(conditionTerm.getParent() instanceof ConditionTerms))
			report_info("Obradjuje se uslov", conditionTerm);
	}

	public void visit(ConditionTerms conditionTerms) {
		boolean error = false;
		if (conditionTerms.getCondTerm().obj.getType() != boolType.getType()) {
			report_error("Greska na liniji " + conditionTerms.getLine() + " : Operator '||' "
					+ "se koristi samo za iskaze tipa bool.", null);
			error = true;
		}
		if (conditionTerms.getCondition().obj.getType() != boolType.getType()) {
			report_error("Greska na liniji " + conditionTerms.getLine() + " : Operator '||' "
					+ "se koristi samo za iskaze tipa bool.", null);
			error = true;
		}
		if (!(conditionTerms.getParent() instanceof ConditionTerms) && !error)
			report_info("Obradjuje se uslov", conditionTerms);
		conditionTerms.obj = conditionTerms.getCondTerm().obj;
	}

	// =======================================DO-WHILE=========================================//
	public void visit(DoWhileStart doWhileStart) {
		doWhile++;
		report_info("Odradjuje se do-while petlja", doWhileStart);
	}

	public void visit(BreakStatement breakStatement) {
		if (doWhile==0) {
			report_error("Greska na liniji " + breakStatement.getLine() + " : Break ne moze da se"
					+ " pozove izvan do-while petlje.", null);
		}
	}

	public void visit(DoWhileStatement doWhileStatement) {
		doWhile--;
		Obj c = doWhileStatement.getCondition().obj;
		if (c != null) {
			if (c.getType() != boolType.getType()) {
				report_error("Greska na liniji " + doWhileStatement.getLine() + " : Uslov unutar "
						+ "while(...) mora biti tipa bool.", null);
			}
		}
	}

	// =======================================IF-ELSE=========================================//
	public void visit(MatchedElse matchedElse) {
		report_info("Obradjuje se if-else grananje", matchedElse);
		if (matchedElse.getIfStart().getCondition().obj.getType() != boolType.getType()) {
			report_error(
					"Greska na liniji " + matchedElse.getLine() + " : Uslov unutar " + "if(...) mora biti tipa bool.",
					null);
		}
	}

	public void visit(UnmatchedElse unmatchedElse) {
		report_info("Obradjuje se if-else grananje", unmatchedElse);
		if (unmatchedElse.getIfStart().getCondition().obj.getType() != boolType.getType()) {
			report_error(
					"Greska na liniji " + unmatchedElse.getLine() + " : Uslov unutar " + "if(...) mora biti tipa bool.",
					null);
		}
	}

	public void visit(UnmatchedIf unmatchedIf) {
		report_info("Obradjuje se if-else grananje", unmatchedIf);
		if (unmatchedIf.getIfStart().getCondition().obj.getType() != boolType.getType()) {
			report_error(
					"Greska na liniji " + unmatchedIf.getLine() + " : Uslov unutar " + "if(...) mora biti tipa bool.",
					null);
		}
	}

	// =======================================READ=========================================//

	public void visit(ReadStatement readStatement) {
		int type = readStatement.getDesignator().obj.getType().getKind();

		report_info("Obradjuje se sistemska funkcija READ", readStatement);

		if (type != Tab.intType.getKind() && type != Tab.charType.getKind() && type != boolType.getType().getKind()) {
			report_error("Greska na liniji " + readStatement.getLine() + " : Tip unutar "
					+ "read(...) mora biti tipa bool, int ili char.", null);
		}
	}

	// =======================================RETURN=========================================//
	public void visit(Return returnElem) {
		if (currentMethod == null) {
			report_error("Greska na liniji " + returnElem.getLine() + " : Return ne moze da se pozove izvan metoda.",
					null);
		}
		returnFound = true;
	}

	public void visit(ReturnEmptyStatement returnEmptyStatement) {
		if (currentMethod.getType() != Tab.noType) {
			report_error("Greska na liniji " + returnEmptyStatement.getLine()
					+ " : Povratna vrednost funkcije mora da vraca" + " vrednost tipa funkcije.", null);
		}
	}

	public void visit(ReturnExprStetement returnExprStetement) {
		if (!currentMethod.getType().compatibleWith(returnExprStetement.getExpr().obj.getType())) {
			report_error("Greska na liniji " + returnExprStetement.getLine() + " : Vrednost u return iskazu "
					+ "ne odgovara povratnoj vrednosti funkcije.", null);
		}
	}

	// =======================================PRINT=========================================//
	public void visit(NoNumPrintStatement print) {
		int kind = print.getExpr().obj.getType().getKind();

		report_info("Obradjuje se sistemska funkcija PRINT", print);

		if (kind != Tab.intType.getKind() && kind != Tab.charType.getKind()) {
			report_error("Greska na liniji " + print.getLine() + " : Iskaz u pozivu funkcije "
					+ "print mora biti tipa char ili int.", null);
			return;
		}
		printCallCount++;
	}

	public void visit(NumPrintStatement print) {
		int kind = print.getExpr().obj.getType().getKind();

		report_info("Obradjuje se sistemska funkcija PRINT", print);

		if (kind != Tab.intType.getKind() && kind != Tab.charType.getKind()) {
			report_error("Greska na liniji " + print.getLine() + " : Iskaz u pozivu funkcije "
					+ "print mora biti tipa cool, char ili int.", null);
			return;
		}
		printCallCount++;
	}

}
