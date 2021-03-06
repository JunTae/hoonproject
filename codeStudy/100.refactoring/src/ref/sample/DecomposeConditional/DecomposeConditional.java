/**
 * @FileName  : Person.java
 * @Project     : code refactoring exam proj
 * @Date         : 2014. 5. 8. 
 * @작성자      : codelife
 * @변경이력 :
 * @프로그램 설명 :
 */
package ref.sample.DecomposeConditional;

public class DecomposeConditional {

	public int compute(int cond , int a , int b, int c)
	{
		int r= 0 ;
		
		if ( condition1(cond, a, b) ) 
		{
			r = calc(a, b)/2;
		}
		else if ( condition1(cond, b, c))
		{
			r = calc(a, b)*2;
		}
		else
		{
			r = calc(a, b);
		}
		
		
		return r;
		
	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	protected int calc(int a, int b) {
		return a*b;
	}

	/**
	 * @param cond
	 * @param a
	 * @param b
	 * @return
	 */
	protected boolean condition1(int cond, int a, int b) {
		return cond > a  || cond > b;
	}
	
}
