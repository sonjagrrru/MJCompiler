// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:57


package rs.ac.bg.etf.pp1.ast;

public class DoWhileEnd implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public DoWhileEnd () {
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
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DoWhileEnd(\n");

        buffer.append(tab);
        buffer.append(") [DoWhileEnd]");
        return buffer.toString();
    }
}
