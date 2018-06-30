package Utils;

public class NumberUtil {
	public static int parseIntWithDefault(String s, int default_val) {
		try{
			return Integer.parseInt(s);
		}
		catch (Exception e){
			return default_val;
		}
	}

	public static boolean isInt(String s){
		try{
			Integer.parseInt(s);
			return true;
		}catch (Exception e)
		{
			return false;
		}
	}
}
