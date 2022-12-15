// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:56


package rs.ac.bg.etf.pp1.ast;

public class ClassName implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Obj obj = null;

    private String className;
    private HasExtends HasExtends;

    public ClassName (String className, HasExtends HasExtends) {
        this.className=className;
        this.HasExtends=HasExtends;
        if(HasExtends!=null) HasExtends.setParent(this);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className=className;
    }

    public HasExtends getHasExtends() {
        return HasExtends;
    }

    public void setHasExtends(HasExtends HasExtends) {
        this.HasExtends=HasExtends;
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
        if(HasExtends!=null) HasExtends.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(HasExtends!=null) HasExtends.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(HasExtends!=null) HasExtends.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassName(\n");

        buffer.append(" "+tab+className);
        buffer.append("\n");

        if(HasExtends!=null)
            buffer.append(HasExtends.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassName]");
        return buffer.toString();
    }
}
