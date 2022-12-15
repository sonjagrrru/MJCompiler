// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:56


package rs.ac.bg.etf.pp1.ast;

public class MethodDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private MethodName MethodName;
    private MethodArgs MethodArgs;
    private FunctionEnd FunctionEnd;

    public MethodDecl (MethodName MethodName, MethodArgs MethodArgs, FunctionEnd FunctionEnd) {
        this.MethodName=MethodName;
        if(MethodName!=null) MethodName.setParent(this);
        this.MethodArgs=MethodArgs;
        if(MethodArgs!=null) MethodArgs.setParent(this);
        this.FunctionEnd=FunctionEnd;
        if(FunctionEnd!=null) FunctionEnd.setParent(this);
    }

    public MethodName getMethodName() {
        return MethodName;
    }

    public void setMethodName(MethodName MethodName) {
        this.MethodName=MethodName;
    }

    public MethodArgs getMethodArgs() {
        return MethodArgs;
    }

    public void setMethodArgs(MethodArgs MethodArgs) {
        this.MethodArgs=MethodArgs;
    }

    public FunctionEnd getFunctionEnd() {
        return FunctionEnd;
    }

    public void setFunctionEnd(FunctionEnd FunctionEnd) {
        this.FunctionEnd=FunctionEnd;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodName!=null) MethodName.accept(visitor);
        if(MethodArgs!=null) MethodArgs.accept(visitor);
        if(FunctionEnd!=null) FunctionEnd.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodName!=null) MethodName.traverseTopDown(visitor);
        if(MethodArgs!=null) MethodArgs.traverseTopDown(visitor);
        if(FunctionEnd!=null) FunctionEnd.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodName!=null) MethodName.traverseBottomUp(visitor);
        if(MethodArgs!=null) MethodArgs.traverseBottomUp(visitor);
        if(FunctionEnd!=null) FunctionEnd.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDecl(\n");

        if(MethodName!=null)
            buffer.append(MethodName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethodArgs!=null)
            buffer.append(MethodArgs.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FunctionEnd!=null)
            buffer.append(FunctionEnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDecl]");
        return buffer.toString();
    }
}
