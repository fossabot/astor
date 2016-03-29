package fr.inria.astor.core.entities;

import org.apache.log4j.Logger;

import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpaceStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.util.StringUtil;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * Operation applied to a particular modification point
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ModificationInstance {
	
	protected Logger log = Logger.getLogger(ModificationInstance.class.getName());

	/**
	 * Gen where the operation is applied
	 */
	private ModificationPoint modificationPoint = null;
	/**
	 * Original element where the operation is applied
	 */
	private CtElement original = null;
	/**
	 * New element of the modification
	 */
	private CtElement modified = null;

	/**
	 * Kind of the mutation Operation applied
	 */
	private AstorOperator operator = null;
	/**
	 * Parent entity there the mut operation
	 */
	private CtBlock<?> parentBlock = null;

	/**
	 * Place where the operation is applied in parent
	 */
	private int locationInParent = -1;

	/**
	 * if an exception occurres where the operation is applied, we save it
	 */
	private Exception exceptionAtApplied = null;
	private boolean successfulyApplied = true;
	
	private IngredientSpaceStrategy ingredientScope = null;
	
	public ModificationInstance (){}
	
	public ModificationInstance(ModificationPoint modificationPoint, AstorOperator operationApplied, CtElement original, CtElement modified) {
		super();
		this.modificationPoint = modificationPoint;
		this.operator = operationApplied;
		this.original = original;
		this.modified = modified;		
	}

	public CtElement getOriginal() {
		return original;
	}

	public void setOriginal(CtElement original) {
		this.original = original;
	}

	public CtElement getModified() {
		return modified;
	}

	public void setModified(CtElement modified) {
		this.modified = modified;
	}

	public CtBlock getParentBlock() {
		return parentBlock;
	}

	public void setParentBlock(CtBlock parentBlock) {
		this.parentBlock = parentBlock;
	}

	public AstorOperator getOperationApplied() {
		return operator;
	}

	public void setOperationApplied(AstorOperator operationApplied) {
		this.operator = operationApplied;
	}

	public Exception getExceptionAtApplied() {
		return exceptionAtApplied;
	}

	public void setExceptionAtApplied(Exception exceptionAtApplied) {
		this.exceptionAtApplied = exceptionAtApplied;
	}

	public String toString() {
		return "" + this.getOperationApplied() + ": `" + StringUtil.trunc(this.original) + " ` ---> `" + StringUtil.trunc(modified) +"` at pos "+getLocationInParent()+" of parent `"+ StringUtil.trunc(parentBlock)
				+ "`";
	}

	public ModificationPoint getModificationPoint() {
		return modificationPoint;
	}

	public void setModificationPoint(ModificationPoint modificationPoint) {
		this.modificationPoint = modificationPoint;
	}

	public int getLocationInParent() {
		return locationInParent;
	}

	public void setLocationInParent(int locationInParent) {
		this.locationInParent = locationInParent;
	}

	public boolean isSuccessfulyApplied() {
		return successfulyApplied;
	}

	public void setSuccessfulyApplied(boolean successfulyApplied) {
		this.successfulyApplied = successfulyApplied;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modificationPoint == null) ? 0 : modificationPoint.hashCode());
		result = prime * result + ((modified == null) ? 0 : modified.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((original == null) ? 0 : original.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModificationInstance other = (ModificationInstance) obj;
		if (modificationPoint == null) {
			if (other.modificationPoint != null)
				return false;
		} else if (!modificationPoint.equals(other.modificationPoint))
			return false;
		if (modified == null) {
			if (other.modified != null)
				return false;
		} else if (!modified.equals(other.modified))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (original == null) {
			if (other.original != null)
				return false;
		} else if (!original.equals(other.original))
			return false;
		return true;
	}

	public IngredientSpaceStrategy getIngredientScope() {
		return ingredientScope;
	}

	public void setIngredientScope(IngredientSpaceStrategy ingredientScope) {
		this.ingredientScope = ingredientScope;
	}

	public void applyModification() {
		//todo opflex
		operator.applyChangesInModel(this, this.getModificationPoint().getProgramVariant());
	}

	
	public void undoModification() {
		//todo opflex
		operator.undoChangesInModel(this, this.getModificationPoint().getProgramVariant());
	}
	
	public void updateProgramVariant() {
		//todo opflex
		operator.updateProgramVariant(this, this.getModificationPoint().getProgramVariant());
	}
	
	public void defineParentInformation(SuspiciousModificationPoint genSusp) {
		CtElement targetStmt = genSusp.getCodeElement();
		CtElement cparent = targetStmt.getParent();
		if ((cparent != null && (cparent instanceof CtBlock))) {
			CtBlock parentBlock = (CtBlock) cparent;
			this.setParentBlock(parentBlock);
			int location = locationInParent(parentBlock, genSusp.getSuspicious().getLineNumber(), targetStmt);
			this.setLocationInParent(location);

		} else {
			log.error("Parent diferent to block");
		}
	}
		
		/**
		 * Return the position of the element in the block. It searches the same
		 * object instance
		 * 
		 * @param parentBlock
		 * @param line
		 * @param element
		 * @return
		 */
		private int locationInParent(CtBlock parentBlock, int line, CtElement element) {
			int pos = 0;
			for (CtStatement s : parentBlock.getStatements()) {
				if (s == element)// the same object
					return pos;
				pos++;
			}

			log.error("Error: parent not found");
			return -1;

		}
}
