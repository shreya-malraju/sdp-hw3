import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

// Define the Visitor interface
interface PayrollVisitor {
    void visit(FullTimeEmployee fullTimeEmployee);
    void visit(PartTimeEmployee partTimeEmployee);
    void visit(ContractEmployee contractEmployee);
}

// Define the AveragePayVisitor and RangeVisitor
class AveragePayVisitor implements PayrollVisitor {
    private double totalFullTimePay = 0;
    private double totalPartTimePay = 0;
    private double totalContractPay = 0;
    private int countFullTime = 0;
    private int countPartTime = 0;
    private int countContract = 0;

    @Override
    public void visit(FullTimeEmployee fullTimeEmployee) {
        totalFullTimePay += fullTimeEmployee.computePay();
        countFullTime++;
    }

    @Override
    public void visit(PartTimeEmployee partTimeEmployee) {
        totalPartTimePay += partTimeEmployee.computePay();
        countPartTime++;
    }

    @Override
    public void visit(ContractEmployee contractEmployee) {
        totalContractPay += contractEmployee.computePay();
        countContract++;
    }

    public double getAverageFullTimePay() {
        return countFullTime > 0 ? totalFullTimePay / countFullTime : 0;
    }

    public double getAveragePartTimePay() {
        return countPartTime > 0 ? totalPartTimePay / countPartTime : 0;
    }

    public double getAverageContractPay() {
        return countContract > 0 ? totalContractPay / countContract : 0;
    }
}

class RangeVisitor implements PayrollVisitor {
    private double minFullTime = Double.MAX_VALUE;
    private double maxFullTime = Double.MIN_VALUE;
    private double minPartTime = Double.MAX_VALUE;
    private double maxPartTime = Double.MIN_VALUE;
    private double minContract = Double.MAX_VALUE;
    private double maxContract = Double.MIN_VALUE;

    @Override
    public void visit(FullTimeEmployee fullTimeEmployee) {
        double payroll = fullTimeEmployee.computePay();
        minFullTime = Math.min(minFullTime, payroll);
        maxFullTime = Math.max(maxFullTime, payroll);
    }

    @Override
    public void visit(PartTimeEmployee partTimeEmployee) {
        double payroll = partTimeEmployee.computePay();
        minPartTime = Math.min(minPartTime, payroll);
        maxPartTime = Math.max(maxPartTime, payroll);
    }

    @Override
    public void visit(ContractEmployee contractEmployee) {
        double payroll = contractEmployee.computePay();
        minContract = Math.min(minContract, payroll);
        maxContract = Math.max(maxContract, payroll);
    }

    public double getMinFullTimePay() {
        return minFullTime;
    }

    public double getMaxFullTimePay() {
        return maxFullTime;
    }

    public double getMinPartTimePay() {
        return minPartTime;
    }

    public double getMaxPartTimePay() {
        return maxPartTime;
    }

    public double getMinContractPay() {
        return minContract;
    }

    public double getMaxContractPay() {
        return maxContract;
    }
}

// Define the Employee class hierarchy
abstract class Employee {
    protected String name;

    public Employee(String name) {
        this.name = name;
    }

    // Accept method for the Visitor pattern
    public abstract void accept(PayrollVisitor visitor);

    // Expert method to compute employee's payroll
    public abstract double computePay();
}

class FullTimeEmployee extends Employee {
    private double grossIncome;
    private double healthcareDeduction;
    private double k401kDeduction;
    private double taxWithholdingRate;

    public FullTimeEmployee(String name, double grossIncome, double healthcareDeduction, double k401kDeduction, double taxWithholdingRate) {
        super(name);
        this.grossIncome = grossIncome;
        this.healthcareDeduction = healthcareDeduction;
        this.k401kDeduction = k401kDeduction;
        this.taxWithholdingRate = taxWithholdingRate;
    }

    @Override
    public void accept(PayrollVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public double computePay() {
        return (grossIncome - healthcareDeduction - k401kDeduction) * (1 - taxWithholdingRate);
    }
}

class PartTimeEmployee extends Employee {
    private double hourlyRate;
    private double hoursWorked;
    private double taxWithholdingRate;

    public PartTimeEmployee(String name, double hourlyRate, double hoursWorked, double taxWithholdingRate) {
        super(name);
        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
        this.taxWithholdingRate = taxWithholdingRate;
    }

    @Override
    public void accept(PayrollVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public double computePay() {
        return hourlyRate * hoursWorked * (1 - taxWithholdingRate);
    }
}

class ContractEmployee extends Employee {
    private double contractTotal;
    private int contractPeriod;

    public ContractEmployee(String name, double contractTotal, int contractPeriod) {
        super(name);
        this.contractTotal = contractTotal;
        this.contractPeriod = contractPeriod;
    }

    @Override
    public void accept(PayrollVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public double computePay() {
        return contractTotal / (double) contractPeriod;
    }
}

// Employee data manager
class EmployeeDataManager {
    // Simulate fetching employee data from a database
    public List<Employee> getEmployees() {
        List<Employee> employees = List.of(
                new FullTimeEmployee("FullTimeEmployee1", 60000, 5000, 2000, 0.2),
                new FullTimeEmployee("FullTimeEmployee2", 80000, 6000, 3000, 0.15),
                new FullTimeEmployee("FullTimeEmployee3", 70000, 5500, 2500, 0.18),
                new PartTimeEmployee("PartTimeEmployee1", 25, 30, 0.15),
                new PartTimeEmployee("PartTimeEmployee2", 22, 20, 0.12),
                new PartTimeEmployee("PartTimeEmployee3", 28, 35, 0.17),
                new ContractEmployee("ContractEmployee1", 24000, 24),
                new ContractEmployee("ContractEmployee2", 18000, 18),
                new ContractEmployee("ContractEmployee3", 30000, 30)
        );
        return employees;
    }
}

// PayrollGUI class
public class PayrollApplication extends JFrame {

    private JTextArea outputArea;
    private EmployeeDataManager dataManager;

    public PayrollApplication() {
        dataManager = new EmployeeDataManager();
        outputArea = new JTextArea(20, 60);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        JButton computePayrollButton = new JButton("Compute Payroll");
        JButton averagePeriodPayButton = new JButton("Show Average Period Pay");
        JButton rangePeriodPayButton = new JButton("Show Range of Period Pay");

        computePayrollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                computePayroll();
            }
        });

        averagePeriodPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAveragePeriodPay();
            }
        });

        rangePeriodPayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRangePeriodPay();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(computePayrollButton);
        buttonPanel.add(averagePeriodPayButton);
        buttonPanel.add(rangePeriodPayButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setTitle("Payroll Application");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    class PayrollVisitorClass implements PayrollVisitor {
        private StringBuilder result = new StringBuilder();

        @Override
        public void visit(FullTimeEmployee fullTimeEmployee) {
            result.append(fullTimeEmployee.name).append(": ").append(fullTimeEmployee.computePay()).append("\n");
        }

        @Override
        public void visit(PartTimeEmployee partTimeEmployee) {
            result.append(partTimeEmployee.name).append(": ").append(partTimeEmployee.computePay()).append("\n");
        }

        @Override
        public void visit(ContractEmployee contractEmployee) {
            result.append(contractEmployee.name).append(": ").append(contractEmployee.computePay()).append("\n");
        }

        public String getResult() {
            return result.toString();
        }
    }


    private void computePayroll() {
        outputArea.setText("");
        List<Employee> employees = dataManager.getEmployees();
        PayrollVisitorClass payrollVisitorClass = new PayrollVisitorClass();

        for (Employee employee : employees) {
            employee.accept(payrollVisitorClass);
        }

        outputArea.append("Payroll for each employee:\n");
        outputArea.append(payrollVisitorClass.getResult());
    }

    private void showAveragePeriodPay() {
        outputArea.setText("");
        List<Employee> employees = dataManager.getEmployees();
        AveragePayVisitor averagePayVisitor = new AveragePayVisitor();

        for (Employee employee : employees) {
            employee.accept(averagePayVisitor);
        }

        double averageFullTimePay = averagePayVisitor.getAverageFullTimePay();
        double averagePartTimePay = averagePayVisitor.getAveragePartTimePay();
        double averageContractPay = averagePayVisitor.getAverageContractPay();

        outputArea.append("Average Period Pay:\n");
        outputArea.append("Full-time employees: " + averageFullTimePay + "\n");
        outputArea.append("Part-time employees: " + averagePartTimePay + "\n");
        outputArea.append("Contract employees: " + averageContractPay + "\n");
    }

    private void showRangePeriodPay() {
        outputArea.setText("");
        List<Employee> employees = dataManager.getEmployees();
        RangeVisitor rangeVisitor = new RangeVisitor();

        for (Employee employee : employees) {
            employee.accept(rangeVisitor);
        }


        double minFullTimePay = rangeVisitor.getMinFullTimePay();
        double maxFullTimePay = rangeVisitor.getMaxFullTimePay();
        double minPartTimePay = rangeVisitor.getMinPartTimePay();
        double maxPartTimePay = rangeVisitor.getMaxPartTimePay();
        double minContractPay = rangeVisitor.getMinContractPay();
        double maxContractPay = rangeVisitor.getMaxContractPay();

        outputArea.append("Range of Period Pay:\n");
        outputArea.append("Full-time employees: Min = " + minFullTimePay + ", Max = " + maxFullTimePay + "\n");
        outputArea.append("Part-time employees: Min = " + minPartTimePay + ", Max = " + maxPartTimePay + "\n");
        outputArea.append("Contract employees: Min = " + minContractPay + ", Max = " + maxContractPay + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PayrollApplication();
        });
    }
}
