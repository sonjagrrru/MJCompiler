package rs.ac.bg.etf.pp1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	// adresa pocetka izvrsavanja programa
	private int mainPC;

	private SemanticAnalyzer s;
	private HashMap<String, LinkedList<Obj>> allMethodDeclarations;

	// ako operand==false preskoci ceo AND iskaz jer je false
	private Stack<Address> skipAnd = new Stack<>();

	// if operand==true preskoci ceo OR iskaz jer je true
	private Stack<Address> skipOr = new Stack<>();

	// u slucaju da je uslov u if-u false preskoci if
	private Stack<Address> falseConditionIf = new Stack<>();

	// u slucaju da je uslov u while-u false preskoci while
	private Stack<Address> falseConditionWhile = new Stack<>();

	// break skokovi na kraj while petlje
	private Stack<Address> breakStack = new Stack<>();

	// continue skok na uslov while petlje
	private Stack<Address> continueStack = new Stack<>();

	// skokovi na kraj elsa
	private Stack<Address> ifElseEnd = new Stack<>();

	// skokovi na nullish dodelu
	private Stack<Address> nullishAssign = new Stack<>();
	private Stack<Address> nullishSecond = new Stack<>();
	boolean nullishOperation = false;

	//adrese pocetka do tela, while uslova i kraja while uslova respektivno
	int doStart = 0;
	int whileStart = 0;
	int whileEnd = 0;
	
	//da li ispitujemo while ili if uslov
	boolean isWhile = false;

	//nivoi ugnezdavanja grananja i petlji
	int if_level = 0;
	int while_level = 0;

	//broj stvarno navedenih argumenata pri pozivu funkcije
	int actualCounter = 0;
	
	//broj opcionih atributa funkcije
	int optionCounter = 0;

	//da li se trenutno obradjuje READ funkcija
	boolean readFlag = false;

	Logger log = Logger.getLogger(getClass());

	public CodeGenerator(SemanticAnalyzer s) {
		this.s = s;
		allMethodDeclarations = s.getAllMethodDeclarations();

		// ********* len *********//

		Obj obj = Tab.find("len");
		obj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(obj.getLevel());
		Code.put(obj.getLocalSymbols().size());
		Code.put(Code.load_n);
		Code.put(Code.arraylength);
		Code.put(Code.exit);
		Code.put(Code.return_);

		// ********* chr *********//

		obj = Tab.find("chr");
		obj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(obj.getLevel());
		Code.put(obj.getLocalSymbols().size());
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);

		// ********* ord *********//

		obj = Tab.find("ord");
		obj.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(obj.getLevel());
		Code.put(obj.getLocalSymbols().size());
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);

	}

	public int getMainPc() {
		return mainPC;
	}

	// =============================METODA============================//

	public void visit(TypeMethodName typeMethodName) {

		String name = typeMethodName.getMethName();

		if ("main".equalsIgnoreCase(name)) {
			mainPC = Code.pc;
		}

		typeMethodName.obj.setAdr(Code.pc);

		int formParsNum = typeMethodName.obj.getLevel();
		int locals = typeMethodName.obj.getLocalSymbols().size();

		// Generisi ulaz metode
		Code.put(Code.enter);
		Code.put(formParsNum);
		Code.put(locals);

	}

	public void visit(VoidMethodName voidMethodName) {
		String name = voidMethodName.getMethName();

		if ("main".equalsIgnoreCase(name)) {
			mainPC = Code.pc;
		}

		voidMethodName.obj.setAdr(Code.pc);

		int formParsNum = voidMethodName.obj.getLevel();
		int locals = voidMethodName.obj.getLocalSymbols().size();

		// Generisi ulaz metode
		Code.put(Code.enter);
		Code.put(formParsNum);
		Code.put(locals);

	}

	public void visit(MethodDecl methodDecl) {
		// za slucaj da void metoda nema return iskaz
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(OptArg optArg) {
		Code.put(Code.pop);
	}

	public void visit(OptArgs optArgs) {
		Code.put(Code.pop);
	}

	// ===============================RETURN============================//

	public void visit(ReturnEmptyStatement statement) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(ReturnExprStetement stetement) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	// ===============================PRINT===============================//

	public void visit(NoNumPrintStatement statement) {
		if (statement.getExpr().obj.getType() == Tab.intType) {
			// razmak izmedju karaktera na izlazu
			Code.loadConst(5);
			Code.put(Code.print);
		} else {
			// razmak izmedju karaktera na izlazu
			Code.loadConst(5);
			Code.put(Code.bprint);
		}
	}

	public void visit(NumPrintStatement statement) {
		int num = statement.getPrintNum();
		Code.loadConst(num);
		if (statement.getExpr().obj.getType() == Tab.intType) {
			Code.put(Code.print);
		} else {
			Code.put(Code.bprint);
		}
	}

	// ===============================READ===================================//

	public void visit(ReadStatement statement) {
		if (statement.getDesignator().obj.getType().equals(Tab.charType))
			Code.put(Code.bread);
		else
			Code.put(Code.read);

		Code.store(statement.getDesignator().obj);
		readFlag = false;
	}

	public void visit(ReadStart readStart) {
		readFlag = true;
	}

	// ==============================TERM=====================================//

	public void visit(SingleNegativeTerm term) {
		Code.put(Code.neg);
	}

	// =============================EXPRESSION=================================//.

	public void visit(NullishOperator nullishOperator) {
		if (!(nullishOperator.getParent() instanceof NullishOperator)) {
			if (nullishOperation) {
				for (int i = 0; i < nullishAssign.size(); i++) {
					if (nullishAssign.get(i).getLevel() == 0) {
						nullishAssign.get(i).setLevel(-1);
						Code.fixup(nullishAssign.get(i).getAdr());
					}
				}
			}
			nullishOperation = false;
		}
	}

	public void visit(Nullish nullish) {
		nullishOperation = true;
		Code.put(Code.dup);
		Code.loadConst(0);
		Code.putFalseJump(Code.eq, 0);
		nullishAssign.add(new Address(Code.pc - 2, 0));
		Code.put(Code.pop);
	}

	public void visit(SingleExpr singleExpr) {
		if (singleExpr.getParent() instanceof NullishOperator) {
			for (int i = 0; i < nullishSecond.size(); i++) {
				if (nullishSecond.get(i).getLevel() == 0) {
					nullishSecond.get(i).setLevel(-1);
					Code.fixup(nullishSecond.get(i).getAdr());
				}
			}
		}
	}

	// ==============================FACTOR===================================//
	// koliko se puta vrsi + ili - operacija toliko puta se pojavi MultipleExprTerm
	// na steku se u tom momentu nalaze ucitana 2 operanda

	public void visit(MultipleTermExpr term) {
		if (term.getAddop() instanceof Add) {
			Code.put(Code.add);
		} else
			Code.put(Code.sub);
	}

	// koliko se puta vrsi *, / ili % operacija toliko puta se pojavi
	// MultipleExprTerm
	// na steku se u tom momentu nalaze ucitana 2 operanda
	public void visit(FactorList list) {
		if (list.getMulop() instanceof Mul) {
			Code.put(Code.mul);
		} else if (list.getMulop() instanceof Div) {
			Code.put(Code.div);
		} else
			Code.put(Code.rem);
	}

	// na steku se u tom momentu nalaze ucitani adresa i velicina niza
	public void visit(NewExprFactor factor) {
		Code.put(Code.newarray);
		if (factor.getType().struct.equals(Tab.charType))
			Code.put(0);
		else
			Code.put(1);
	}

	// =============================KONSTANTE===========================//

	public void visit(NumConst numConst) {
		Code.loadConst(numConst.getNumVal());
	}

	public void visit(CharConst charConst) {
		Code.loadConst(charConst.getCharVal());
	}

	public void visit(BoolConst boolConst) {
		Code.loadConst(boolConst.getBoolVal() ? 1 : 0);
	}

	// za klase i recorde ne testira se
	public void visit(NewFactor factor) {
		Code.put(Code.new_);
		Code.put2(factor.getType().struct.getNumberOfFields() * 4);
	}

	// ==========================DESIGNATORS================================//

	// na steku se nalze operandi sa leve i desne strane dodele vrednosti
	public void visit(DesignatorAssign assign) {
		Code.store(assign.getDesignator().obj);
	}

	// na steku se nalazi operand koji se inkrementira
	public void visit(DesignatorIncrement increment) {

		// else Code.load(increment.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(increment.getDesignator().obj);
	}

	// na steku se nalazi operand koji se dekrementira
	public void visit(DesignatorDecrement decrement) {

		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(decrement.getDesignator().obj);
	}

	// ==========================POZIV METODE=================================//
	// metode ustvari imaju fiksan broj argumenata ali ako se pri pozivu opcioni
	// argumenti izostave pri pozivu se koriste podrazumevane vrednosti za te
	// argumente

	public void visit(DesignatorNonParamsFactor factor) {
		String methodName = factor.getDesignator().getDesignatorName().getDesignatorName();
		LinkedList<Obj> formals = allMethodDeclarations.get(methodName);

		actualCounter = formals.size() - actualCounter;

		// dodaj na stek podrazumevane vrednosti argumenata ako nisu navedeni
		while (actualCounter > 0) {
			Obj tmp = formals.get(actualCounter - 1);
			Code.loadConst(tmp.getAdr());
			actualCounter--;
		}
		actualCounter = 0;
		int adr = factor.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(adr);
	}

	public void visit(DesignatorParamsFactor factor) {
		String methodName = factor.getDesignator().getDesignatorName().getDesignatorName();
		LinkedList<Obj> formals = allMethodDeclarations.get(methodName);

		actualCounter = formals.size() - actualCounter;

		// dodaj na stek podrazumevane vrednosti argumenata ako nisu navedeni
		while (actualCounter > 0) {
			Obj tmp = formals.get(actualCounter - 1);
			Code.loadConst(tmp.getAdr());
			actualCounter--;
		}
		actualCounter = 0;
		int adr = factor.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(adr);
	}

	// brojac navedenih argumenata pri pozivu
	public void visit(ActualParameters parameter) {
		actualCounter++;
	}

	// brojac navedenih argumenata pri pozivu
	public void visit(ActualParameter parameter) {
		actualCounter++;
	}

	public void visit(Designator designator) {
		if (designator.getParent() instanceof DesignatorAssign || designator.obj.getKind() != Obj.Elem || readFlag)
			return;

		// zbog instrukcije ucitavanja vrednosti u niz
		if (designator.getParent() instanceof DesignatorIncrement && designator.obj.getKind() == Obj.Elem)
			Code.put(Code.dup2);
		if (designator.getParent() instanceof DesignatorDecrement && designator.obj.getKind() == Obj.Elem)
			Code.put(Code.dup2);

		Code.load(designator.obj);
	}

	// ucitavanje promenljive na stek (u slucaju niza ucitava adresu niza)
	public void visit(DesignatorName name) {
		if (name.obj.getKind() == Obj.Meth || (name.getParent().getParent() instanceof DesignatorAssign
				&& name.obj.getType().getKind() != Struct.Array))
			return;
		Code.load(name.obj);
	}

	// =========================CONDITIONS===============================//

	public int operatorCode(Relop relop) {
		if (relop instanceof Same)
			return Code.eq;
		if (relop instanceof Differ)
			return Code.ne;
		if (relop instanceof Greater)
			return Code.gt;
		if (relop instanceof GreaterEqual)
			return Code.ge;
		if (relop instanceof Less)
			return Code.lt;
		return Code.le;
	}

	// ako je true udji u if inace skoci
	public void visit(ExprFact fact) {
		Code.loadConst(0);
		Code.putFalseJump(Code.ne, 0); // ako je jednako 0 onda skaci
		skipAnd.push(new Address(Code.pc - 2, if_level));
	}

	// Expr ovaj Nullish SingleExpr

	// na steku se nalaze dva operanda
	// ako uslov nije ispunjen preskace se ceo AND iskaz zato se stavlja falseJump
	public void visit(RelopFact relopFact) {
		int operation = this.operatorCode(relopFact.getRelop());
		switch (operation) {
		case 0:
			Code.putFalseJump(Code.eq, 0);
			break;
		case 1:
			Code.putFalseJump(Code.ne, 0);
			break;
		case 2:
			Code.putFalseJump(Code.lt, 0);
			break;
		case 3:
			Code.putFalseJump(Code.le, 0);
			break;
		case 4:
			Code.putFalseJump(Code.gt, 0);
			break;
		default:
			Code.putFalseJump(Code.ge, 0);
			break;
		}

		// pc pokazuje iza jmp instrukcije trenutno
		skipAnd.push(new Address(Code.pc - 2, if_level));
	}

	// ako je AND iskaz ispao true preskacemo ceo OR iskaz i idemo na izvrsavanje IF
	// grane
	public void visit(ConditionFacts facts) {
		if (facts.getParent() instanceof ConditionFacts)
			return;
		Code.putJump(0);
		skipOr.add(new Address(Code.pc - 2, if_level));
		for (int i = 0; i < skipAnd.size(); i++) {
			if (skipAnd.get(i).getLevel() == if_level) {
				skipAnd.get(i).setLevel(-10);
				Code.fixup(skipAnd.get(i).getAdr());
			}
		}
	}

	// ako je AND iskaz ispao true preskacemo ceo OR iskaz i idemo na izvrsavanje IF
	// grane
	public void visit(ConditionFact fact) {
		if (fact.getParent() instanceof ConditionFacts)
			return;
		Code.putJump(0);
		skipOr.add(new Address(Code.pc - 2, if_level));
		for (int i = 0; i < skipAnd.size(); i++) {
			if (skipAnd.get(i).getLevel() == if_level) {
				skipAnd.get(i).setLevel(-10);
				Code.fixup(skipAnd.get(i).getAdr());
			}
		}
	}

	// ako je uslov ispao false preskaci if blok u suprotnom udji u if blok
	public void visit(ConditionTerms terms) {
		if (terms.getParent() instanceof ConditionTerms)
			return;
		Code.putJump(0);
		if (isWhile)
			falseConditionWhile.add(new Address(Code.pc - 2, while_level));
		else
			falseConditionIf.add(new Address(Code.pc - 2, if_level));

		// pocetak if bloka
		for (int i = 0; i < skipOr.size(); i++) {
			if (skipOr.get(i).getLevel() == if_level) {
				Code.fixup(skipOr.get(i).getAdr());
				skipOr.get(i).setLevel(-10);
			}
		}
	}

	// ako je uslov ispao false preskaci if blok u suprotnom udji u if blok
	public void visit(ConditionTerm term) {
		if (term.getParent() instanceof ConditionTerms)
			return;
		Code.putJump(0);
		if (isWhile) {
			falseConditionWhile.add(new Address(Code.pc - 2, while_level));
		} else
			falseConditionIf.add(new Address(Code.pc - 2, if_level));

		// pocetak if bloka
		for (int i = 0; i < skipOr.size(); i++) {
			if (skipOr.get(i).getLevel() == if_level) {
				Code.fixup(skipOr.get(i).getAdr());
				skipOr.get(i).setLevel(-10);
			}
		}
	}

	// ==================================IF===============================//

	public void visit(IfTerminal ifTerminal) {
		if_level++;
	}

	public void visit(ElseDeclaration elseDeclaration) {

		// na pocetak elsa dodajemo skok za kraj elsa za one koji su usli u if granu
		Code.putJump(0);
		ifElseEnd.add(new Address(Code.pc - 2, if_level));

		// prepravi sve adrese koje skacu na else granu
		for (int i = 0; i < falseConditionIf.size(); i++) {
			if (falseConditionIf.get(i).getLevel() == if_level) {
				Code.fixup(falseConditionIf.get(i).getAdr());
				falseConditionIf.get(i).setLevel(-10);
			}
		}
	}

	public void visit(MatchedElse matchedElse) {

		// prepravi skokove za kraj else grane
		for (int i = 0; i < falseConditionIf.size(); i++) {
			if (falseConditionIf.get(i).getLevel() == if_level) {
				Code.fixup(falseConditionIf.get(i).getAdr());
				falseConditionIf.get(i).setLevel(-10);
			}
		}
		for (int i = 0; i < ifElseEnd.size(); i++) {
			if (ifElseEnd.get(i).getLevel() == if_level) {
				Code.fixup(ifElseEnd.get(i).getAdr());
				ifElseEnd.get(i).setLevel(-10);
			}
		}
		if_level--;
	}

	public void visit(UnmatchedElse unmatchedElse) {

		// prepravi skokove za kraj else grane
		for (int i = 0; i < falseConditionIf.size(); i++) {
			if (falseConditionIf.get(i).getLevel() == if_level) {
				Code.fixup(falseConditionIf.get(i).getAdr());
				falseConditionIf.get(i).setLevel(-10);
			}
		}
		for (int i = 0; i < ifElseEnd.size(); i++) {
			if (ifElseEnd.get(i).getLevel() == if_level) {
				Code.fixup(ifElseEnd.get(i).getAdr());
				ifElseEnd.get(i).setLevel(-10);
			}
		}
		if_level--;
	}

	public void visit(UnmatchedIf unmatchedIf) {

		// skok na kraj if grane koja nema else granu
		for (int i = 0; i < falseConditionIf.size(); i++) {
			if (falseConditionIf.get(i).getLevel() == if_level) {
				Code.fixup(falseConditionIf.get(i).getAdr());
				falseConditionIf.get(i).setLevel(-10);
			}

		}
		if_level--;
	}

	// ==============================WHILE=================================//

	public void visit(DoWhileStart doWhileStart) {
		doStart = Code.pc;
		while_level++;
	}

	// pc pokazuje iza do-whilea
	public void visit(DoWhileEnd doWhileEnd) {
		whileEnd = Code.pc;
		isWhile = false;
		Code.putJump(doStart);
		for (int i = 0; i < breakStack.size(); i++) {
			if (breakStack.get(i).getLevel() == while_level) {
				Code.fixup(breakStack.get(i).getAdr());
				breakStack.get(i).setLevel(-10);
			}

		}
		for (int i = 0; i < falseConditionWhile.size(); i++) {
			if (falseConditionWhile.get(i).getLevel() == while_level) {
				Code.fixup(falseConditionWhile.get(i).getAdr());
				falseConditionWhile.get(i).setLevel(-10);
			}

		}
		while_level--;
	}

	// prepravi adrese za continue: trenutni pc je pocetak do-while tela
	public void visit(WhileStart whileStart) {
		isWhile = true;
		for (int i = 0; i < continueStack.size(); i++) {
			if (continueStack.get(i).getLevel() == while_level) {
				Code.fixup(continueStack.get(i).getAdr());
				continueStack.get(i).setLevel(-10);
			}

		}
	}

	// skoci na kraj whilea
	public void visit(BreakStatement breakStatement) {
		Code.putJump(0);
		breakStack.add(new Address(Code.pc - 2, while_level));
	}

	// skoci na pocetak do-while tela
	public void visit(ContinueStatement continueStatement) {
		Code.putJump(0);
		continueStack.add(new Address(Code.pc - 2, while_level));
	}

}
