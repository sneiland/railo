package railo.intergral.fusiondebug.server.type.nat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import railo.commons.lang.ClassUtil;
import railo.intergral.fusiondebug.server.type.FDValueNotMutability;
import railo.intergral.fusiondebug.server.type.simple.FDSimpleVariable;
import railo.runtime.op.Caster;
import railo.runtime.type.ObjectWrap;

import com.intergral.fusiondebug.server.IFDStackFrame;

public class FDNative extends FDValueNotMutability {

	private ArrayList children=new ArrayList();
	
	private String name;

	private Object value;

	/**
	 * Constructor of the class
	 * @param frame 
	 * @param name
	 * @param coll
	 */
	public FDNative(IFDStackFrame frame, String name, Object value) {
		this.name=name;
		
		if(value instanceof ObjectWrap){
			value=((ObjectWrap)value).getEmbededObject(value);
		}
		this.value=value;
		
		Class clazz = value.getClass();
		
		
		// super
		children.add(new FDSimpleVariable(frame,"Extends",ClassUtil.getName(clazz.getSuperclass()),null));
		
		// interfaces
		Class[] faces = clazz.getInterfaces();
		if(faces.length>0){
			ArrayList list = new ArrayList();
			
			children.add(new FDSimpleVariable(frame,"Interfaces","",list));
			for(int i=0;i<faces.length;i++){
				list.add(new FDSimpleVariable(frame,"["+(i+1)+"]",ClassUtil.getName(faces[i]),null));
			}
		}
		ArrayList el,list;
		
		// fields
		Field[] flds = clazz.getFields();
		if(flds.length>0){
			Field fld;
			list = new ArrayList();
			children.add(new FDSimpleVariable(frame,"Fields","",list));
			for(int i=0;i<flds.length;i++){
				fld=flds[i];
				el = new ArrayList();
				list.add(new FDSimpleVariable(frame,fld.getName(),"",el));
				el.add(new FDSimpleVariable(frame,"Type",ClassUtil.getName(fld.getType()),null));
				el.add(new FDSimpleVariable(frame,"Modifier",Modifier.toString(fld.getModifiers()),null));
			}
		}
		// methods
		Method[] mths = clazz.getMethods();
		if(mths.length>0){
			Method mth;
			list = new ArrayList();
			children.add(new FDSimpleVariable(frame,"Methods","",list));
			for(int i=0;i<mths.length;i++){
				mth=mths[i];
				el = new ArrayList();
				list.add(new FDSimpleVariable(frame,mth.getName(),"",el));
				
				el.add(new FDSimpleVariable(frame,"Modifier",Modifier.toString(mth.getModifiers()),null));
				
				// exceptions
				Class[] clsTypes = mth.getExceptionTypes();
				if(clsTypes.length>0){
					ArrayList exps = new ArrayList();
					el.add(new FDSimpleVariable(frame,"Exceptions",Caster.toString(clsTypes.length),exps));
					for(int y=0;y<clsTypes.length;y++){
						exps.add(new FDSimpleVariable(frame,"["+(y+1)+"]",ClassUtil.getName(clsTypes[y]),null));
					}
				}
				
				// params
				Class[] clsParams = mth.getParameterTypes();
				if(clsParams.length>0){
					ArrayList params = new ArrayList();
					el.add(new FDSimpleVariable(frame,"Parameters",Caster.toString(clsParams.length),params));
					for(int y=0;y<clsParams.length;y++){
						params.add(new FDSimpleVariable(frame,"["+(y+1)+"]",ClassUtil.getName(clsParams[y]),null));
					}
				}
				
				// return
				el.add(new FDSimpleVariable(frame,"Return",ClassUtil.getName(mth.getReturnType()),null));
			}
		}
	}

	@Override
	public List getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public String toString() {
		return Caster.toClassName(value);
	}
}
