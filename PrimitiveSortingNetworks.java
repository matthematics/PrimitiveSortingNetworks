import java.util.*;
import java.math.*;

/* Class Chinese: representation of large integer as a list of remainders
	Purpose is to speed computations with large numbers */
class Chinese {
	static int mods[]; //global list of moduli
	int res[]; //residues of this number with respect to the moduli
	
	/* setMods: set the global moduli */
	public static void setMods(int[] mods2)
	{
		mods=mods2;
	}
	
	/* Constructor: Chinese()
	 * creates a new Chinese object with value 0
	 */
	public Chinese()
	{
		res=new int[mods.length];
	}
	
	public int hashCode()
	{
		return res[0]+res[res.length-1];
	}
	
	public boolean equals(Object o)
	{
		Chinese c=(Chinese)o;
		return Arrays.equals(c.res,res);
	}

	/* addTo: add the argument to this in place */
	public void addTo(Chinese b)
	{
		for(int i=0;i<res.length;i++)
		{
			res[i]+=b.res[i];
			
			if(res[i]>=mods[i])
				res[i]-=mods[i];
			
		}
	}
	
	/* add: returns a new Chinese object representing the sum of
	 * this and the argument
	 */
	public Chinese add(Chinese b)
	{
		Chinese c=new Chinese();
		System.arraycopy(res,0,c.res,0,res.length);
		
		for(int i=0;i<res.length;i++)
		{
			c.res[i]+=b.res[i];
			
			if(c.res[i]>=mods[i])
				c.res[i]-=mods[i];
			
		}
		return c;
	}
	
	/* negateIn: negates this object in place */
	public void negateIn()
	{
		for(int i=0;i<res.length;i++)
			if(res[i]!=0)
				res[i]=mods[i]-res[i];
	}
	
	/* negate: returns a new Chinese object whose value is the negative of this */
	public Chinese negate()
	{
		Chinese c=new Chinese();
		System.arraycopy(res,0,c.res,0,res.length);
		
		for(int i=0;i<res.length;i++)
			if(c.res[i]!=0)
				c.res[i]=mods[i]-c.res[i];
		return c;
	}
	
	/* toBigInt: converts this object to a BigInteger with the same value */
	public BigInteger toBigInt()
	{
		BigInteger bigmods[]=new BigInteger[mods.length];
		
		for(int i=0;i<mods.length;i++)
		{
			bigmods[i] = new BigInteger((new Integer(mods[i])).toString());
		}
		
		BigInteger sum=new BigInteger("0");
		BigInteger product=new BigInteger("1");
		
		for(int j=0;j<mods.length;j++)
		{
			product=product.multiply(bigmods[j]);
			BigInteger toAdd=new BigInteger((new Integer(res[j])).toString());
			//multiply by mod*mod^-1
			for(int k=0;k<mods.length;k++)
			{
				if(k!=j)
				{
					toAdd=toAdd.multiply(bigmods[k]);
					toAdd=toAdd.multiply(bigmods[k].modInverse(bigmods[j]));
				}
			}
			sum=sum.add(toAdd);
		}
		sum=sum.mod(product);
		return sum;
	}
}

/* Class Perm: permutation of the integers 1,2,...,n 
 * Supports composition, inversion, reversing portions, and
 * generating permutations in order 
 * IMPORTANT: Indexing starts at 1, not at 0! */
class Perm {
	int data[];
	int size; //the length of the permutation
	
	/* Perm(int n): constructor returning the identity permutation of length n */
	public Perm(int n)
	{
		size=n;
		
		data=new int[size];
		for(int i=1;i<=n;i++)
			set(i,i);
	}
	
	/* Perm(Perm p): copy constructor */
	public Perm(Perm p)
	{
		size=p.size;
		data=new int[p.data.length];
		System.arraycopy(p.data,0,data,0,data.length);
	}
	
	/* get(int i): returns the value at position i*/
	public int get(int i)
	{
		return data[i-1];
	}
	
	/* set(int i, int a): sets the value at position i to a */
	public void set(int i, int a)
	{
		data[i-1]=a;
	}
	
	/* reverse(int i, int j): reverse the portion of the permutation from index i to index j */
	public void reverse(int i, int j)
	{
		int swap=0;
		for(int k=i;k<=(i+j)/2;k++)
		{
			swap=get(k);
			set(k,get(i+j-k));
			set(i+j-k,swap);
		}
	}
	
	/* swap(int i): swaps the values at the indices i and i+1 */
	public void swap(int i)
	{
		int swap=get(i);
		set(i,get(i+1));
		set(i+1,swap);
	}
	
	/*hashCode(): returns the hash code
	 * Note that this hash function is essentially perfect (no collisions) within the limits
	 * of a 32-bit integer
	 */
	public int hashCode()
	{
		int sum=0;
		
		for(int i=1;i<=size;i++)
		{
			sum*=size+1;
			sum+=get(i);
		}
		return sum;
	}
	
	public boolean equals(Object o)
	{
		Perm p=(Perm)o;
		
		return Arrays.equals(data,p.data);
	}
	
	/* inverse(): returns a new Perm object representing the inverse permutation */
	public Perm inverse()
	{
		Perm p=new Perm(size);
		
		for(int i=1;i<=size;i++)
		{
			p.set(get(i),i);
		}
		return p;
	}
	
	/* compose(Perm p): returns a new Perm object representing the composition of this with p */
	public Perm compose(Perm p)
	{
		Perm p2=new Perm(size);
		
		for(int i=1;i<=size;i++)
		{
			p2.set(i,get(p.get(i)));
		}
		return p2;
	}
	
	public int length()
	{
		return size;
	}
	
	/* next(): changes this Perm object in place to the next permutation
	 * in an ordering compatible with weak order
	 * For the purposes of this program, the function traverses a topological
	 * sorting of the directed graph of all permutations of the same length
	 */
	public boolean next()
	{
		int seq[]=new int[length()-1];
		boolean good=false;
		for(int i=1;i<=length()-1;i++)
		{
			if(get(i)>get(i+1))
			{
				
				for(int j=i;j>=1&&get(j)>get(j+1);j--,seq[i-1]++)
				{
					swap(j);
				}		
			}
		}
		
		int lastnonempty=length()-2;
	
		for(;lastnonempty>=0;lastnonempty--)
		{
			if(seq[lastnonempty]>0)
			{
				break;
			}
		}	
		
		if(lastnonempty<length()-2&&lastnonempty!=-1)
		{
			seq[lastnonempty]--;
			seq[lastnonempty+1]++;
		}
		else
		{
			//find last nonempty column such that next column is not maxed out
	
			int col=0;
			int maxcol=-1;
	
			for(;col<length()-2;col++)
			{
				if(seq[col]>0&&seq[col+1]<col+2)
				{
					maxcol=col;
				}
			}
	
			//are we at the end?
	
			col=maxcol;
	
			if(col==-1)
			{
				//sum it up and redistribute
				int sum=0;
		
				for(int i=0;i<length()-1;i++)
				{
					sum+=seq[i];
				}
		
				int ret=sum++;
		
				for(int i=0;i<length()-1;i++)
				{
					if(sum<i+1)
					{
						seq[i]=sum;
						sum=0;
					}
					else
					{
						seq[i]=i+1;
						sum-=i+1;
					}
				}
			}
			else
			{
				seq[col]--;
		
				int sum=0;
		
				for(int i=col+1;i<length()-1;i++)
				{
					sum+=seq[i];
				}
		
				sum++;
		
				for(int i=col+1;i<length()-1;i++)
				{
					if(sum<i+1)
					{
						seq[i]=sum;
						sum=0;
					}
					else
					{
						seq[i]=i+1;
						sum-=i+1;
					}
				}
		
			}
		}
		//have the next sequence
		
		for(int i=length()-2;i>=0;i--)
		{
			for(int j=i-seq[i]+1;j<=i;j++)
			{
				swap(j+1);
			}
		}
		return true;
	}
	
	public String toString()
	{
		String ret="";
		
		for(int i=1;i<=length();i++)
			ret+=get(i);
		return ret;
	}
}

/* Class PrimitiveSortingNetworks
 * Main class in an application whose purpose is to compute the number
 * of primitive sorting networks on n elements
 * The algorithm is an iterative version of the recursive Maple code 
 * posted by Matthew J. Samuel at https://oeis.org/A006245
 */
public class PrimitiveSortingNetworks
{
	public static void main(String args[])
	{
		if(args.length==0)
		{
			System.out.println("PrimitiveSortingNetworks: Compute the number of primitive sorting networks on n elements");
			System.out.println("Usage: PrimitiveSortingNetworks n [modulus1 modulus2 modulus3 ...]");
			return;
		}
		Perm p=new Perm(Integer.parseInt(args[0]));
		HashMap<Perm,Chinese> results=new HashMap(); //store intermediate results
		HashMap<Perm,Perm> perms=new HashMap(); //store permutations already used to avoid massive numbers of duplicates
		
		int mods[] = {1073741824,1073741823,1073741821}; //default moduli
	
		if(args.length>1) //if more than one argument, user decided to supply his or her own moduli
		{
			mods=new int[args.length-1];
			for(int i=0;i<args.length-1;i++)
				mods[i]=Integer.parseInt(args[i+1]);
		}
		
		Chinese.setMods(mods);
	
		Chinese ONE=new Chinese(); //the number 1
		
		for(int i=0;i<mods.length;i++)
			ONE.res[i]=1;
			
		results.put(p,ONE);
		
		Chinese lastresult=ONE;
		
		long time=System.currentTimeMillis();
		
		while(results.size()>0)
		{
			lastresult=results.remove(p);
			perms.remove(p);
			
			Vector<Perm> nodes=new Vector();
			Vector<Integer> spots=new Vector();
			Vector<Boolean> negs=new Vector();
			
			nodes.add(p);
			spots.add(new Integer(1));
			negs.add(Boolean.TRUE);
			
			while(nodes.size()!=0)
			{
				boolean neg=negs.remove(negs.size()-1).booleanValue();
				int spot=spots.remove(spots.size()-1).intValue();
				Perm node=nodes.remove(nodes.size()-1);
				
				neg=!neg;
				for(int i=spot;i<node.length();i++)
				{
					if(node.get(i)<node.get(i+1))
					{
						Perm addnode=null;
						
						perms.remove(node);
						
						node.swap(i);
						Chinese stuff=null;
						
						if(perms.containsKey(node))
							addnode=perms.get(node);
						else
						{
							addnode=new Perm(node);
							perms.put(addnode,addnode);
						}
						
						node.swap(i);
						
						perms.put(node,node);
						
						if(results.containsKey(addnode))
						{
							stuff=results.get(addnode);
						}
						else
						{
							stuff=new Chinese();
							results.put(addnode,stuff);
						}
						
						if(neg)
						{
							lastresult.negateIn();
							stuff.addTo(lastresult);
							lastresult.negateIn();
						}
						else
						{
							stuff.addTo(lastresult);
						}
						negs.add(neg);
						spots.add(new Integer(i+2));
						nodes.add(addnode);
					}
				}
				
			}
			p.next();
		}
		System.out.println("Time: "+(System.currentTimeMillis()-time)+" milliseconds");
		System.out.println("Result: "+lastresult.toBigInt());
	}
}