package controller;

//import java.io.Console;
import java.io.PrintWriter;
import java.io.StringWriter;
import model.domain.Boat;
import model.domain.Boat.BoatType;
import model.domain.Member;
import model.domain.MemberRegistry;
import model.persistence.MemberDatabase;
import view.ConsoleUI;

/**
 * "Main" class. It is in this class the program is running
 */
public class User {

  /**
   * Fetches stored data and puts in the memberregistry.
   */
  public void initiateData(MemberRegistry memberReg) {
    ConsoleUI ui = new ConsoleUI();

    try {
      MemberDatabase memberDb = new MemberDatabase();
      memberDb.loadData(memberReg);

    } catch (Exception e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String stackTrace = sw.toString(); // convert stacktrace to string;
      ui.printError(stackTrace); // print exception
    }

  }

  /**
   * starts the application.
   */
  public void runApp(MemberRegistry memberReg, String appName) {

    ConsoleUI ui = new ConsoleUI();
    ui.printHeader(appName); // present app name

    boolean exit = false;

    while (!exit) {
      ui.printMainMenu();

      switch (ui.collectUserChoice(3, "a selection or 0 to exit")) {
        case 0:
          exit = true;
          break;
        case 1:
          createMember(memberReg);
          break;
        case 2:
          showVerboseMemberList(memberReg);
          break;
        case 3:
          showCompactMemberList(memberReg);
          break;
        default:
          break;
      }
    }
  }

  /**
   * Starts the process of creating a new member.
   */
  private void createMember(MemberRegistry memberReg) {
    ConsoleUI ui = new ConsoleUI();
    String firstName = ui.collectString("first name");
    String lastName = ui.collectString("last name");
    long personalNumber = ui.collectLong("personal number (10 digits)");

    try {
      int memberId = memberReg.addMember(firstName, lastName, personalNumber);
      Member m = memberReg.getMember(memberId); // fetch member to confirm creation

    } catch (NullPointerException e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String stackTrace = sw.toString(); // convert stacktrace to string;
      ui.printError(stackTrace);
    }
    ui.confirmation("member", "created");
  }

  /**
   * Displays a verbose memberlist.
   */
  private void showVerboseMemberList(MemberRegistry memberReg) {
    ConsoleUI ui = new ConsoleUI();
    for (Member m : memberReg.getMemberList()) {
      ui.printMember(m);
      if (m.getNumberOfBoats() > 0) {
        ui.printBoats(m);
      }
    }
  }

  /**
   * Displays a compact memberlist.
   */
  private void showCompactMemberList(MemberRegistry memberReg) {
    ConsoleUI ui = new ConsoleUI();

    ui.printCompactList(memberReg);
    int input = ui.collectUserChoice(memberReg.getNumberOfMembers(), "memberID or 0 to go back");

    while (input != 0) {
      ui.printMemberOptions();
      Member m = memberReg.getMember(input); // fetch member
      switch (ui.collectUserChoice(4, "a selection or 0 to go back")) {
        case 0:
          input = 0;
          break;
        case 1:
          ui.printMember(m);
          if (m.getNumberOfBoats() > 0) {
            ui.printBoats(m);
          }
          break;
        case 2:
          changeMember(m);
          break;
        case 3:
          registerBoat(m);
          break;
        case 4:
          if (deleteMember(memberReg, input)) {
            input = 0;
          }
          break;
        default:
          break;
      }
    }
  }

  /**
   * Delete a member object.
   */
  private Boolean deleteMember(MemberRegistry memberReg, int memberId) {
    ConsoleUI ui = new ConsoleUI();
    ui.printAreYouSure("you want to delete this member");
    if (ui.collectUserChoice(2, "a selection") == 2) {
      memberReg.deleteMember(memberId);
      ui.confirmation("member", "deleted");
      return true;
    }
    return false;
  }

  /**
   * Change a member objects information.
   */
  private void changeMember(Member m) {
    ConsoleUI ui = new ConsoleUI();
    int input = -1;

    while (input != 0) {
      ui.printChangeMenu("First name", "Last name", "Boats");
      input = ui.collectUserChoice(3, "a selection or 0 to go back");
      switch (input) {
        case 0:
          input = 0;
          break;
        case 1:
          String firstName = ui.collectString("new first name");
          m.setFirstName(firstName);
          ui.confirmation("first name", "changed");
          break;
        case 2:
          String lastName = ui.collectString("new last name");
          m.setLastName(lastName);
          ui.confirmation("last name", "changed");
          break;
        case 3:
          boatMenu(m);
          break;
        default:
          break;
      }
    }
  }

  /**
   * Starts the process of register a new boat on a member.
   */
  private void boatMenu(Member m) {
    ConsoleUI ui = new ConsoleUI();
    if (m.getNumberOfBoats() > 0) {
      ui.printBoats(m);
    }

    int input = ui.collectUserChoice(m.getNumberOfBoats(), "a selection or 0 to go back");
    // int input = ui.collectInteger("boat number or 0 to go back");

    while (input != 0) {
      ui.printBoatOptions();
      Boat b = m.getBoat(input);
      switch (ui.collectUserChoice(2, "a selection or 0 to go back")) {
        case 0:
          input = 0;
          break;
        case 1:
          changeBoat(b);
          break;
        case 2:
          ui.printAreYouSure("you want to delete this boat");
          if (ui.collectUserChoice(2, "a selection or 0 to go back") == 2) {
            m.deleteBoat(input);
            ui.confirmation("boat", "deleted");
          }
          input = 0;
          break;
        default:
          break;
      }
    }
  }

  /**
   * Starts the process of register a new boat on a member.
   */
  private void registerBoat(Member m) {
    ConsoleUI ui = new ConsoleUI();
    String name = ui.collectString("name");
    Double lenght = ui.collectDouble("boat lenght");
    ui.printBoatTypes();
    int numberOfTypes = Boat.BoatType.values().length - 2;
    int i = ui.collectUserChoice(numberOfTypes, "a selection"); // collects user choise
    BoatType type = Boat.BoatType.values()[i]; // set variable value depending on user choise

    try {
      Boat b = new Boat(name, type, lenght);
      m.addBoat(b);
      // Member m = memberReg.getMember(memberId); // fetch member to confirm creation

    } catch (NullPointerException e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String stackTrace = sw.toString(); // convert stacktrace to string;
      ui.printError(stackTrace);
    }
    ui.confirmation("boat", "registred");

  }

  /**
   * Starts the process of changing boat information.
   */
  private void changeBoat(Boat b) {
    ConsoleUI ui = new ConsoleUI();
    int input = -1;

    while (input != 0)
      ;
    ui.printChangeMenu("Name", "Length", "Boat type");
    input = ui.collectUserChoice(3, "a selection or 0 to go back");
    switch (input) {
      case 0:
        input = 0;
        break;
      case 1:
        String name = ui.collectString("new name");
        b.setName(name);
        ui.confirmation("name", "changed");
        break;
      case 2:
        Double length = ui.collectDouble("lenght");
        b.setLength(length);
        ui.confirmation("length", "changed");
        break;
      case 3:
        changeBoatType(b);
        break;
      default:
        break;
    }
  }

  /**
   * Change a boats type.
   */
  private void changeBoatType(Boat b) {
    ConsoleUI ui = new ConsoleUI();
    ui.printBoatTypes();

    // get number of options from enum (-2 because of count at end of enum and array
    // start with 0)
    int numberOfTypes = Boat.BoatType.values().length - 2;

    int i = ui.collectUserChoice(numberOfTypes, "a selection"); // collects user choise
    BoatType type = Boat.BoatType.values()[i]; // set variable value depending on user choise
    b.setBoatType(type);
    ui.confirmation("type", "changed");
  }

}
