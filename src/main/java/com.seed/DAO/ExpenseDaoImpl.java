package com.seed.DAO;

import com.seed.Model.Expense;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class ExpenseDaoImpl implements ExpenseDao {

    @Override
    public void createExpense(Expense newExpense) {
        try(Connection connection = ConnectionFactory.createConnection();){

            PreparedStatement statement = connection.prepareStatement("insert into ERSIO.ERS_REIMBURSEMENTS(R_AMOUNT, R_DESCRIPTION, " +
                    "R_SUBMITTED, R_RESOLVED, U_ID_AUTHOR, U_ID_RESOLVER, RT_TYPE, RT_STATUS) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, newExpense.getAmount());
            statement.setString(2, newExpense.getDescriptor());

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/DD/YYYY");
            LocalDate localDate = LocalDate.now();
            statement.setString(3, localDate.toString());

            statement.setString(4, "");//Resolved date is empty when created

            statement.setString(5, newExpense.getIdAuthor());
            statement.setString(6, "");//new expense cannot be resolved
            statement.setString(7, newExpense.getType());
            statement.setString(8, "3");//set status to 3 for pending

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Expense> retrieveExpenses() {
        try(Connection connection = ConnectionFactory.createConnection();){

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT R_ID, R_AMOUNT, R_DESCRIPTION, R_SUBMITTED, R_RESOLVED, U_ID_AUTHOR, U_ID_RESOLVER, B.RT_TYPE, C.RS_STATUS AS RT_STATUS " +
                            "FROM ERSIO.ERS_REIMBURSEMENTS A " +
                            "LEFT JOIN ERSIO.ERS_REIMBURSEMENT_TYPE B " +
                            "ON A.RT_TYPE=B.RT_ID " +
                            "LEFT JOIN ERSIO.ERS_REIMBURSEMENT_STATUS C " +
                            "ON A.RT_STATUS=C.RS_STATUS" +
                            "WHERE RT_STATUS <> 3");

            PreparedStatement statementNoJoins = connection.prepareStatement("SELECT R_ID, R_AMOUNT, R_DESCRIPTION, R_SUBMITTED, R_RESOLVED, U_ID_AUTHOR, U_ID_RESOLVER, RT_TYPE, RT_STATUS " +
                    "FROM ERSIO.ERS_REIMBURSEMENTS A" +
                    "WHERE RT_STATUS <> 3");

            ResultSet resultset = statementNoJoins.executeQuery();

            List<Expense> list = new LinkedList<>();
            while(resultset.next()) {
                String expenseID = resultset.getString("R_ID");
                String amount = resultset.getString("R_AMOUNT");
                String descriptor = resultset.getString("R_DESCRIPTION");
                String submitted = resultset.getString("R_SUBMITTED");
                String resolved = resultset.getString("R_RESOLVED");
                String idAuthor = resultset.getString("U_ID_AUTHOR");
                String resolver = resultset.getString("U_ID_RESOLVER");
                String type = resultset.getString("RT_TYPE");
                String status = resultset.getString("RT_STATUS");

                Expense temp = new Expense(expenseID, amount, descriptor,submitted,resolved, idAuthor, resolver, type, status);
                list.add(temp);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Expense> retrievePendingExpenses() {
        try(Connection connection = ConnectionFactory.createConnection();){

            PreparedStatement statement = connection.prepareStatement("SELECT R_ID, R_AMOUNT, R_DESCRIPTION, R_SUBMITTED, R_RESOLVED, U_ID_AUTHOR, U_ID_RESOLVER, RT_TYPE, RT_STATUS " +
                    "FROM ERSIO.ERS_REIMBURSEMENTS A WHERE RT_STATUS = 3");

            ResultSet resultset = statement.executeQuery();

            List<Expense> list = new LinkedList<>();
            while(resultset.next()) {
                String expenseID = resultset.getString("R_ID");
                String amount = resultset.getString("R_AMOUNT");
                String descriptor = resultset.getString("R_DESCRIPTION");
                String submitted = resultset.getString("R_SUBMITTED");
                String resolved = resultset.getString("R_RESOLVED");
                String idAuthor = resultset.getString("U_ID_AUTHOR");
                String resolver = resultset.getString("U_ID_RESOLVER");
                String type = resultset.getString("RT_TYPE");
                String status = resultset.getString("RT_STATUS");

                Expense temp = new Expense(expenseID, amount, descriptor,submitted,resolved, idAuthor, resolver, type, status);
                list.add(temp);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void resolveExpense(String R_ID, String RS_STATUS, String managerUserName){
        try(Connection connection = ConnectionFactory.createConnection();){

            PreparedStatement statement = connection.prepareStatement("update ERSIO.ERS_REIMBURSEMENTS A" +
                    "SET RT_STATUS=?," +
                    "U_ID_RESOLVER=?," +
                    "R_RESOLVED=?" +
                    "WHERE R_ID=?");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/DD/YYYY");
            LocalDate localDate = LocalDate.now();

            statement.setString(1, RS_STATUS);
            statement.setString(2, managerUserName);
            statement.setString(3, localDate.toString());
            statement.setString(4, R_ID);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<String> retrieveExpenseTypes(){
        try(Connection connection = ConnectionFactory.createConnection();){

            PreparedStatement statement = connection.prepareStatement(
                    "SELECT RT_TYPE FROM ERSIO.ERS_REIMBURSEMENT_TYPE GROUP BY 1");

            ResultSet resultset = statement.executeQuery();
            List<String> list = new LinkedList<>();

            while(resultset.next()) {
                String type = resultset.getString("RT_TYPE");
                list.add(type);
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
