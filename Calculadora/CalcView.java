package Calculadora;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CalcView extends JFrame {
    private static final String INITIAL_VALUE = "1";

    private JTextField m_userInputTf = new JTextField(5);
    private JTextField m_totalTf     = new JTextField(20);
    private JButton    m_calcBtn     = new JButton("Calcular");
    private JButton    m_clearBtn    = new JButton("Clear");
    private JComboBox<String> m_operationCb = new JComboBox<>(new String[]{"+", "-", "*"});

    public CalcView() {
        m_totalTf.setText(INITIAL_VALUE);
        m_totalTf.setEditable(false);

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Input"));
        content.add(m_userInputTf);
        content.add(m_operationCb);
        content.add(m_calcBtn);
        content.add(new JLabel("Total"));
        content.add(m_totalTf);
        content.add(m_clearBtn);

        this.setContentPane(content);
        this.pack();
        this.setTitle("Simple Calc - MVC");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void reset() {
        m_totalTf.setText(INITIAL_VALUE);
        m_userInputTf.setText("");
        m_operationCb.setSelectedIndex(0);
    }

    String getUserInput() {
        return m_userInputTf.getText();
    }

    String getOperation() {
        return (String) m_operationCb.getSelectedItem();
    }

    void setTotal(String newTotal) {
        m_totalTf.setText(newTotal);
    }

    void showError(String errMessage) {
        JOptionPane.showMessageDialog(this, errMessage);
    }

    void addCalculateListener(ActionListener mal) {
        m_calcBtn.addActionListener(mal);
    }

    void addClearListener(ActionListener cal) {
        m_clearBtn.addActionListener(cal);
    }
}
