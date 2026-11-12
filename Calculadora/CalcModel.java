package Calculadora;
import java.math.BigInteger;

public class CalcModel {
    private static final String INITIAL_VALUE = "1";
    private BigInteger m_total;

    public CalcModel() {
        reset();
    }

    public void reset() {
        m_total = new BigInteger(INITIAL_VALUE);
    }

    public void multiplyBy(String operand) throws NumberFormatException {
        m_total = m_total.multiply(new BigInteger(operand));
    }

    public void add(String operand) throws NumberFormatException {
        m_total = m_total.add(new BigInteger(operand));
    }

    public void subtract(String operand) throws NumberFormatException {
        m_total = m_total.subtract(new BigInteger(operand));
    }

    public String getValue() {
        return m_total.toString();
    }

    public void setValue(String value) {
        m_total = new BigInteger(value);
    }
}