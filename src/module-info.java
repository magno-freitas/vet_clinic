
module vet {
    requires java.base;
    requires java.sql;
   
    requires java.desktop;
    
    requires java.logging;
    requires java.xml;
    exports vet.ui;
    exports vet.util;
    exports vet.model;
    exports vet.service;
}