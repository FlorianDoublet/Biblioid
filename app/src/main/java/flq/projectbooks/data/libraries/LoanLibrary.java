package flq.projectbooks.data.libraries;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import flq.projectbooks.data.Loan;
import flq.projectbooks.database.LoansDataSource;

/**
 * Created by flori on 15/02/2016.
 */
public class LoanLibrary implements Serializable {
    private static LoanLibrary loans;
    private static Context context;
    List<Loan> loanList;
    private LoansDataSource datasource;

    public LoanLibrary() {
        loanList = new ArrayList<>();
        datasource = new LoansDataSource(context);
        datasource.open();
        loanList = datasource.getAllLoans();
        datasource.close();
    }

    public LoanLibrary(Context _context) {
        context = _context;
        loans = new LoanLibrary();
    }

    public static LoanLibrary getInstance() {
        return loans;
    }

    public Loan Add(Loan loan) {
        loanList.add(loan);
        datasource.open();
        Loan new_loan = datasource.createLoan(loan.dateToString(loan.getDateLoan()), loan.dateToString(loan.getDateReminder()), loan.getBook_id(), loan.getFriend_id()); //Add loan to database
        loanList = datasource.getAllLoans(); //Update loans
        datasource.close();
        return new_loan;
    }

    public List<Loan> getLoanList() {
        return loanList;
    }

    public Loan getNewLoan() {
        return new Loan();
    }

    public void deleteLoan(Loan loan) {
        loanList.remove(loan);
    }

    public Loan getLoanById(long id) {
        for (int j = 0; j < loanList.size(); j++) {
            if (loanList.get(j).getId() == id) {
                return loanList.get(j);
            }
        }
        return null;
    }

    //get an loan with his book id
    public Loan getLoanByBookId(long book_id) {
        for (Loan loan : loanList) {
            if (loan.getBook_id() == book_id) {
                return loan;
            }
        }
        return null;
    }

    //get an loan with his book id and friend id
    public Loan getLoanByBookAndFriendId(long book_id, long friend_id) {
        for (Loan loan : loanList) {
            if (loan.getBook_id() == book_id && loan.getFriend_id() == friend_id) {
                return loan;
            }
        }
        return null;
    }

    //get all loan with a friend id
    public List<Loan> getAllLoansByFriendID(long friend_id) {
        List<Loan> loans = new ArrayList<>();
        for (Loan loan : loanList) {
            if (loan.getFriend_id() == friend_id) {
                loans.add(loan);
            }
        }
        return loans;
    }

    public void deleteLoanById(int id) {
        for (int j = 0; j < loanList.size(); j++) {
            if (loanList.get(j).getId() == id) {
                //Remove from database
                Loan temp = loanList.get(j);
                datasource.open();
                datasource.deleteLoan(temp);
                datasource.close();

                //Remove from local list
                loanList.remove(j);

                return;
            }
        }
    }

    public void deleteLoanByLoanId(long loan_id) {
            Loan temp = this.getLoanById(loan_id);
            datasource.open();
            datasource.deleteLoan(temp);
            datasource.close();

            //Remove from local list
            loanList.remove(temp);
    }

    //method used to delete a loan by a book id
    public void deleteLoanByBookId(long book_id){
        Loan temp = null;
        for(Loan loan : loanList){
            if(loan.getBook_id() == book_id){
                temp = loan;
                break;
            }
        }
        if(temp != null){
            datasource.open();
            datasource.deleteLoan(temp);
            datasource.close();

            //Remove from local list
            loanList.remove(temp);
        }
    }


    public void updateLocalList() {
        datasource.open();
        loanList = datasource.getAllLoans();
        datasource.close();
    }


    public long updateOrAddLoan(Loan loan) {
        long id = loan.getId();
        if (id != -1) {
            for (int j = 0; j < loanList.size(); j++) {
                if (loanList.get(j).getId() == id) {
                    datasource.open();
                    datasource.updateLoan(loan); //Update database
                    datasource.close();
                    loanList.set(j, loan); //Update local list
                }
            }
        } else {
            datasource.open();
            id = datasource.createLoan(loan.dateToString(loan.getDateLoan()), loan.dateToString(loan.getDateReminder()), loan.getBook_id(), loan.getFriend_id()).getId(); //Add loan to database
            loanList = datasource.getAllLoans(); //Update loans
            datasource.close();
        }
        return id;
    }

}
