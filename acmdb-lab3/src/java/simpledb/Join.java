package simpledb;

import java.util.*;

/**
 * The Join operator implements the relational join operation.
 */
public class Join extends Operator {

    private static final long serialVersionUID = 1L;

    private JoinPredicate p;
    private DbIterator child1;
    private DbIterator child2;
    /**
     * Constructor. Accepts to children to join and the predicate to join them
     * on
     * 
     * @param p
     *            The predicate to use to join the children
     * @param child1
     *            Iterator for the left(outer) relation to join
     * @param child2
     *            Iterator for the right(inner) relation to join
     */
    public Join(JoinPredicate p, DbIterator child1, DbIterator child2) {
        // some code goes here
        this.p = p;
        this.child1 = child1;
        this.child2 = child2;
    }

    public JoinPredicate getJoinPredicate() {
        // some code goes here
        return p;
    }

    /**
     * @return
     *       the field name of join field1. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField1Name() {
        // some code goes here
        return child1.getTupleDesc().getFieldName(p.getField1());
    }

    /**
     * @return
     *       the field name of join field2. Should be quantified by
     *       alias or table name.
     * */
    public String getJoinField2Name() {
        // some code goes here
        return child2.getTupleDesc().getFieldName(p.getField1());
    }

    /**
     * @see simpledb.TupleDesc#merge(TupleDesc, TupleDesc) for possible
     *      implementation logic.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return TupleDesc.merge(child1.getTupleDesc(), child2.getTupleDesc());
    }

    private Tuple curch1 = null;

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
        super.open();
        child1.open();
        child2.open();
        curch1 = null;
    }

    public void close() {
        // some code goes here
        super.close();
        child1.close();
        child2.close();
        curch1 = null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        child1.rewind();
        child2.rewind();
        curch1 = null;
    }

    /**
     * Returns the next tuple generated by the join, or null if there are no
     * more tuples. Logically, this is the next tuple in r1 cross r2 that
     * satisfies the join predicate. There are many possible implementations;
     * the simplest is a nested loops join.
     * <p>
     * Note that the tuples returned from this particular implementation of Join
     * are simply the concatenation of joining tuples from the left and right
     * relation. Therefore, if an equality predicate is used there will be two
     * copies of the join attribute in the results. (Removing such duplicate
     * columns can be done with an additional projection operator if needed.)
     * <p>
     * For example, if one tuple is {1,2,3} and the other tuple is {1,5,6},
     * joined on equality of the first column, then this returns {1,2,3,1,5,6}.
     * 
     * @return The next matching tuple.
     * @see JoinPredicate#filter
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if(child1 == null || child2 == null)
            return null;
        if(curch1 == null) {
            if(child1.hasNext()) {
                curch1 = child1.next();
            }
            else {
                return null;
            }
        }
        while(curch1 != null) {
            while(child2.hasNext()) {
                Tuple t2 = child2.next();
                if(p.filter(curch1, t2)) {
                    Tuple ans = new Tuple(getTupleDesc());
                    int pos = 0;
                    Iterator<Field> it = curch1.fields();
                    while(it.hasNext()) {
                        ans.setField(pos++, it.next());
                    }
                    it = t2.fields();
                    while(it.hasNext()) {
                        ans.setField(pos++, it.next());
                    }
                    return ans;
                }
            }
            if(child1.hasNext()) {
                curch1 = child1.next();
            }
            else {
                curch1 = null;
            }
            child2.rewind();
        }
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        DbIterator[] children = new DbIterator[2];
        children[0] = child1;
        children[1] = child2;
        return children;
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        child1 = children[0];
        child2 = children[1];
        curch1 = null;
    }

}
