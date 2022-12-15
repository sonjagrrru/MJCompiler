// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:56


package rs.ac.bg.etf.pp1.ast;

public class NonConstructorClassBody extends ClassBody {

    private MethodBody MethodBody;

    public NonConstructorClassBody (MethodBody MethodBody) {
        this.MethodBody=MethodBody;
        if(MethodBody!=null) MethodBody.setParent(this);
    }

    public MethodBody getMethodBody() {
        return MethodBody;
    }

    public void setMethodBody(MethodBody MethodBody) {
        this.MethodBody=MethodBody;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodBody!=null) MethodBody.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodBody!=null) MethodBody.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodBody!=null) MethodBody.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NonConstructorClassBody(\n");

        if(MethodBody!=null)
            buffer.append(MethodBody.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NonConstructorClassBody]");
        return buffer.toString();
    }
}
