package az.aldoziflaj.popmovies;

/**
 * A list of utility methods used through the application
 */
public class Utility {

    /**
     * The method formats a date string from yyyy-MM-dd to dd/MM/yyyy
     * @param unformattedDate Unformated date from the cloud service
     * @return The formatted date
     */
    public static String releaseDateFormatter(String unformattedDate) {
        StringBuilder sb = new StringBuilder();
        String[] explodedDate = unformattedDate.split("-");

        sb.append(explodedDate[2])                      //day of month
                .append("/").append(explodedDate[1])    //month
                .append("/").append(explodedDate[0]);   //year

        return sb.toString();
    }
}
