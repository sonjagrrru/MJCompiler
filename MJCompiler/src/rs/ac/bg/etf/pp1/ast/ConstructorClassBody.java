// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:56


package rs.ac.bg.etf.pp1.ast;

public class ConstructorClassBody extends ClassBody {

    private ConstructorBody ConstructorBody;

    public ConstructorClassBody (ConstructorBody ConstructorBody) {
        this.ConstructorBody=ConstructorBody;
        if(ConstructorBody!=null) ConstructorBody.setParent(this);
    }

    public ConstructorBody getConstructorBody() {
        return ConstructorBody;
    }

    public void setConstructorBody(ConstructorBody ConstructorBody) {
        this.ConstructorBody=ConstructorBody;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstructorBody!=null) ConstructorBody.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstructorBody!=null) ConstructorBody.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstructorBody!=null) ConstructorBody.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstructorClassBody(\n");

        if(ConstructorBody!=null)
            buffer.append(ConstructorBody.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstructorClassBody]");
        return buffer.toString();
    }
}
