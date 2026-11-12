package Calculadora;

import java.awt.event.*;

public class CalcController {
    private CalcModel m_model;
    private CalcView  m_view;

    public CalcController(CalcModel model, CalcView view) {
        m_model = model;
        m_view  = view;

        view.addCalculateListener(new CalculateListener());
        view.addClearListener(new ClearListener());
    }

    class CalculateListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String userInput = "";
            try {
                userInput = m_view.getUserInput();
                String operation = m_view.getOperation();

                switch (operation) {
                    case "+":
                        m_model.add(userInput);
                        break;
                    case "-":
                        m_model.subtract(userInput);
                        break;
                    case "*":
                        m_model.multiplyBy(userInput);
                        break;
                }
                
                m_view.setTotal(m_model.getValue());
            } catch (NumberFormatException nfex) {
                m_view.showError("Bad input: '" + userInput + "'");
            }
        }
    }

    class ClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            m_model.reset();
            m_view.reset();
        }
    }
}
