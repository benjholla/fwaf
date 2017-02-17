# Formal Web Application Firewall (fwaf)
Formal Web Application Firewall (fwaf) is a web application firewall that enables verification and validation of routes and input parameters.

Application routes are modeled as a finite state machine in the form of a [Kripke structure](https://en.wikipedia.org/wiki/Kripke_structure_(model_checking)). The firewall enforces that users follow valid transitions in the given Kripke structure. Provable assertions can then be made about the model in the form of [CTL formulas](https://en.wikipedia.org/wiki/Computation_tree_logic) that can be checked by the [SMART model checker](http://ieeexplore.ieee.org/abstract/document/1348056/). Inputs are validated by their successful parsing into strongly typed Java objects. A set of common input object types are provided with fwaf, but can be extended for any custom input.

Note: This project has nothing to do with the [fWaf project](http://fsecurify.com/fwaf-machine-learning-driven-web-application-firewall/).
