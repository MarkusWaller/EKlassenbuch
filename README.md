# EKlassenbuch
#
#

#
# Einstellungen in xampp/apache/conf/httpd.conf
#
# Am Ende der Datei einfügen und den Pfad entsprechend anpassen.
#

Alias /ecb "C:\Users\Markus\Desktop\Projekt-SAP\EKlassenbuch\server\php"
<Directory "C:\Users\Markus\Desktop\Projekt-SAP\EKlassenbuch\server\php">
   AllowOverride All
   Require all granted
</Directory>

#
# Einstellungen in xampp/php/php.ini
#
# Vorhandene Zeile für "error_reporting" mit folgender ersetzen.
#

error_reporting = E_ALL & ~E_DEPRECATED & ~E_STRICT
