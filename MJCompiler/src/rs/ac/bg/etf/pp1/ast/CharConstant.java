// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:56


package rs.ac.bg.etf.pp1.ast;

public class CharConstant extends ConstVal {

    private CharConst CharConst;

    public CharConstant (CharConst CharConst) {
        this.CharConst=CharConst;
        if(CharConst!=null) CharConst.setParent(this);
    }

    public CharConst getCharConst() {
        return CharConst;
    }

    public void setCharConst(CharConst CharConst) {
        this.CharConst=CharConst;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CharConst!=null) CharConst.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CharConst!=null) CharConst.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CharConst!=null) CharConst.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CharConstant(\n");

        if(CharConst!=null)
            buffer.append(CharConst.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CharConstant]");
        return buffer.toString();
    }
}
