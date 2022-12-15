// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:57


package rs.ac.bg.etf.pp1.ast;

public class NumPrintStatement extends Matched {

    private Expr Expr;
    private int printNum;

    public NumPrintStatement (Expr Expr, int printNum) {
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.printNum=printNum;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public int getPrintNum() {
        return printNum;
    }

    public void setPrintNum(int printNum) {
        this.printNum=printNum;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Expr!=null) Expr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NumPrintStatement(\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+printNum);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NumPrintStatement]");
        return buffer.toString();
    }
}
