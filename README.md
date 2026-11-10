# SplitEven (DIGT 2107)

Desktop expense-sharing app for roommates, trip groups, and small clubs. Users can log expenses, split them (evenly or unevenly), track who owes whom, and settle.

**Team:** Adam Bridges, Edward Drobnis, Francis Kafieh, Claire Bhagat

### Requirements

(todo once project is setup)

## Local data

The app stores CSV files in `.spliteven` inside the current user's home directory
by default. For example, on Windows this is `C:\Users\<name>\.spliteven`.
Keeping app data outside the repository avoids accidentally committing personal
data.

To use a different folder while reviewing or testing, set the Java system
property `spliteven.dataDir` to that folder in the run configuration.

## Running tests

Run the unit tests from the project directory with `sh mvnw clean test`. On
Windows, use `.\mvnw.cmd clean test` in PowerShell.

## Repository layout

```
README.md
src/
├── main/java/       # app code
└── test/java/       # unit tests
docs/
├── deliverables/    # Submitted PDFs
├── requirements/    # Requirements and test tracking
├── testing/         # Test documentation
└── design/          # Diagrams and illustrations
sprint-log.md        # Iteration goals, results, retrospectives
```