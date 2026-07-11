# Phishing Awareness Demo - Fake Login Page Backend

**Course:** OOP Lab (CS3506)
**Student:** Abdul Basit (0001115254)
**Instructor:** Ms. Rabiyah Bibi

## ⚠️ Disclaimer
This project is strictly for educational purposes as part of an academic
OOP assignment. It demonstrates the mechanics of phishing attacks in a
safe, non-functional way — **no real credentials are ever captured, stored,
or transmitted.** The login form submission triggers an educational
explanation screen instead of any data logging.

## What This Project Does
A Java-based HTTP server (built using the JDK's native `com.sun.net.httpserver`
package) serves a realistic-looking fake login page. When a user submits
the form, instead of capturing their input, the server displays an
awareness message explaining what a real phishing attack would have done
at that exact moment.

## Tech Stack
- Java 17 (JDK, no external frameworks)
- HTML/CSS for the frontend login page
- Built-in `HttpServer` class for routing and request handling

## Project Structure