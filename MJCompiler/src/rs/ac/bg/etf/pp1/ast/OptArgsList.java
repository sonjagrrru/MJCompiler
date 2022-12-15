// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:57


package rs.ac.bg.etf.pp1.ast;

public class OptArgsList extends OptArgs {

    private OptArgs OptArgs;
    private OptArg OptArg;

    public OptArgsList (OptArgs OptArgs, OptArg OptArg) {
        this.OptArgs=OptArgs;
        if(OptArgs!=null) OptArgs.setParent(this);
        this.OptArg=OptArg;
        if(OptArg!=null) OptArg.setParent(this);
    }

    public OptArgs getOptArgs() {
        return OptArgs;
    }

    public void setOptArgs(OptArgs OptArgs) {
        this.OptArgs=OptArgs;
    }

    public OptArg getOptArg() {
        return OptArg;
    }

    public void setOptArg(OptArg OptArg) {
        this.OptArg=OptArg;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptArgs!=null) OptArgs.accept(visitor);
        if(OptArg!=null) OptArg.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptArgs!=null) OptArgs.traverseTopDown(visitor);
        if(OptArg!=null) OptArg.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptArgs!=null) OptArgs.traverseBottomUp(visitor);
        if(OptArg!=null) OptArg.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("OptArgsList(\n");

        if(OptArgs!=null)
            buffer.append(OptArgs.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptArg!=null)
            buffer.append(OptArg.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [OptArgsList]");
        return buffer.toString();
    }
}
