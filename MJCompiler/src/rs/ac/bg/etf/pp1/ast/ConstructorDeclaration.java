// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:56


package rs.ac.bg.etf.pp1.ast;

public class ConstructorDeclaration extends ConstructorDecl {

    private ConstructorName ConstructorName;
    private FunctionEnd FunctionEnd;

    public ConstructorDeclaration (ConstructorName ConstructorName, FunctionEnd FunctionEnd) {
        this.ConstructorName=ConstructorName;
        if(ConstructorName!=null) ConstructorName.setParent(this);
        this.FunctionEnd=FunctionEnd;
        if(FunctionEnd!=null) FunctionEnd.setParent(this);
    }

    public ConstructorName getConstructorName() {
        return ConstructorName;
    }

    public void setConstructorName(ConstructorName ConstructorName) {
        this.ConstructorName=ConstructorName;
    }

    public FunctionEnd getFunctionEnd() {
        return FunctionEnd;
    }

    public void setFunctionEnd(FunctionEnd FunctionEnd) {
        this.FunctionEnd=FunctionEnd;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstructorName!=null) ConstructorName.accept(visitor);
        if(FunctionEnd!=null) FunctionEnd.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstructorName!=null) ConstructorName.traverseTopDown(visitor);
        if(FunctionEnd!=null) FunctionEnd.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstructorName!=null) ConstructorName.traverseBottomUp(visitor);
        if(FunctionEnd!=null) FunctionEnd.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstructorDeclaration(\n");

        if(ConstructorName!=null)
            buffer.append(ConstructorName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FunctionEnd!=null)
            buffer.append(FunctionEnd.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstructorDeclaration]");
        return buffer.toString();
    }
}
