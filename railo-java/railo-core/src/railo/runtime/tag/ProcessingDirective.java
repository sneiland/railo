package railo.runtime.tag;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.ext.tag.BodyTagTryCatchFinallyImpl;
import railo.runtime.op.Caster;
import railo.runtime.writer.CFMLWriter;
import railo.runtime.writer.WhiteSpaceWriter;

/**
* Suppresses extra white space and other output, produced by CFML within the tag's scope.
*
*
*
**/
public final class ProcessingDirective extends BodyTagTryCatchFinallyImpl {

	/** A string literal; the character encoding to use to read the page. The value may be enclosed in single or double quotation marks, or none. */
	//private String pageencoding=null;

	private Boolean suppresswhitespace;
    private boolean hasBody;
	
	@Override
	public void release()	{
		super.release();
		//pageencoding=null;
		suppresswhitespace=null;
	}

	/**
	* constructor for the tag class
	**/
	public ProcessingDirective() {
	}

	/** set the value pageencoding
	*  A string literal; the character encoding to use to read the page. The value may be enclosed in single or double quotation marks, or none.
	* @param pageencoding value to set
	**/
	public void setPageencoding(String pageencoding)	{
	    //pageContext. get HttpServletResponse().set ContentType("text/html; charset="+pageencoding);
		//this.pageencoding=pageencoding;
	}

	public void setExecutionlog(boolean executionlog)	{
	}
	public void setPreservecase(boolean b)	{
	}
	
	
	

	/** set the value suppresswhitespace
	*  Boolean indicating whether to suppress the white space and other output generated by the 
	* 		CFML tags within the cfprocessingdirective block.
	* @param suppresswhitespace value to set
	**/
	public void setSuppresswhitespace(boolean suppresswhitespace)	{
		this.suppresswhitespace=Caster.toBoolean(suppresswhitespace);
	}


	@Override
	public int doStartTag() throws ApplicationException	{
		if(suppresswhitespace!=null && !hasBody) {
            throw new ApplicationException
            ("for suppressing whitespaces you must define a end tag for tag [cfprocessingdirective]");
        } 
        if(suppresswhitespace!=null)return EVAL_BODY_BUFFERED;    	
        return EVAL_BODY_INCLUDE;
    }
	
    @Override
    public void doInitBody() {
    }
	
    @Override
    public int doAfterBody() {
		return SKIP_BODY;
    }
    

	/**
	 * sets if tag has a body or not
	 * @param hasBody
	 */
	public void hasBody(boolean hasBody) {
		this.hasBody=hasBody;
	}

    @Override
    public void doFinally() {
    	if(suppresswhitespace!=null) {
    		try {
    			JspWriter out = pageContext.getOut();
	            if(suppresswhitespace.booleanValue()) {
	            	if(out instanceof WhiteSpaceWriter)out.write(bodyContent.getString());
	            	else out.write(StringUtil.suppressWhiteSpace(bodyContent.getString()));
	            }
	            else {
	                if(out instanceof CFMLWriter){
	                	((CFMLWriter)out).writeRaw(bodyContent.getString());
	                }
	                else 
	                	out.write(bodyContent.getString());
	            }
    		} catch (IOException e) {
    			throw new PageRuntimeException(Caster.toPageException(e));
    		}
        }
    }
}