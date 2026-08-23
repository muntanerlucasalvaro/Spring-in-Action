# MicroLoan

App to manage personal microloans for a small lender. It lets you register applicants, create their loan applications, and move them through a review process until they're approved or rejected. The idea is to replace the Excel sheet they use now, where approvals that shouldn't go through slip in and there's no way to know what state each application is in.

## Build

    mvn clean package

## Run

    java -jar target/microloan-1.0-SNAPSHOT.jar