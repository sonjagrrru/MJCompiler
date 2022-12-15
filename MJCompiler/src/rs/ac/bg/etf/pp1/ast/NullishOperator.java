// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:57


package rs.ac.bg.etf.pp1.ast;

public class NullishOperator extends Expr {

    private Expr Expr;
    private Nullish Nullish;
    private SingleExpr SingleExpr;

    public NullishOperator (Expr Expr, Nullish Nullish, SingleExpr SingleExpr) {
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.Nullish=Nullish;
        if(Nullish!=null) Nullish.setParent(this);
        this.SingleExpr=SingleExpr;
        if(SingleExpr!=null) SingleExpr.setParent(this);
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public Nullish getNullish() {
        return Nullish;
    }

    public void setNullish(Nullish Nullish) {
        this.Nullish=Nullish;
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
        if(Expr!=null) Expr.accept(visitor);
        if(Nullish!=null) Nullish.accept(visitor);
        if(SingleExpr!=null) SingleExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(Nullish!=null) Nullish.traverseTopDown(visitor);
        if(SingleExpr!=null) SingleExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(Nullish!=null) Nullish.traverseBottomUp(visitor);
        if(SingleExpr!=null) SingleExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NullishOperator(\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Nullish!=null)
            buffer.append(Nullish.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(SingleExpr!=null)
            buffer.append(SingleExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NullishOperator]");
        return buffer.toString();
    }
}
