// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:57


package rs.ac.bg.etf.pp1.ast;

public class SingleExpression extends Expr {

    private SingleExpr SingleExpr;

    public SingleExpression (SingleExpr SingleExpr) {
        this.SingleExpr=SingleExpr;
        if(SingleExpr!=null) SingleExpr.setParent(this);
    }

    public SingleExpr getSingleExpr() {
        return SingleExpr;
    }

    public void setSingleExpr(SingleExpr SingleExpr) {
        this.SingleExpr=SingleExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(SingleExpr!=null) SingleExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(SingleExpr!=null) SingleExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(SingleExpr!=null) SingleExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleExpression(\n");

        if(SingleExpr!=null)
            buffer.append(SingleExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleExpression]");
        return buffer.toString();
    }
}
