# Formal Web Application Firewall (fwaf)
Formal Web Application Firewall (fwaf) is a web application firewall that enables verification and validation of routes and input parameters.

Application routes are modeled as a finite state machine in the form of a [Kripke structure](https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)). The firewall enforces that users follow valid transitions in the given Kripke structure. Inputs are validated by their successful parsing into strongly typed objects. A set of common input object types are provided with fwaf, but can be extended for any custom input.
