// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:57


package rs.ac.bg.etf.pp1.ast;

public class MethodArgArray extends FormalParamDecl {

    private Type Type;
    private String formalArrName;

    public MethodArgArray (Type Type, String formalArrName) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.formalArrName=formalArrName;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public String getFormalArrName() {
        return formalArrName;
    }

    public void setFormalArrName(String formalArrName) {
        this.formalArrName=formalArrName;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodArgArray(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+formalArrName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodArgArray]");
        return buffer.toString();
    }
}
