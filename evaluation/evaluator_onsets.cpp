#include <iostream>
#include <fstream>
#include <vector>
#include <math.h>

using namespace std;

const double kDEVIATION=0.050; // deviation allowed (in secs)

typedef struct {
  double time;
  bool found;
} onset;


bool onsetOK(double a, double b)
{
	return (b>=a-kDEVIATION && b<=a+kDEVIATION);
}

void Compare(vector<onset> voriginal, vector<onset> vdetected)
{
    int ok=0;
    int fp=0;
    int fn=0;
    int doubled=0;
    int merged=0;
    
    vector<double> meandeviationvector;

    // LEFT TO RIGHT COMPROBATION (ONSETS OK)
    for (vector<onset>::iterator it=voriginal.begin(); it!=voriginal.end(); it++)
    {
    	bool found=false;
	vector<onset>::iterator closest;
	double diff, mindiff=10000;
	
    	for (vector<onset>::iterator it2=vdetected.begin() ; it2!=vdetected.end() ; it2++) 
    	{	
    		if (onsetOK(it->time, it2->time) && it2->found==false) {
    			diff=fabs(it->time-it2->time);
    			if (diff<mindiff) {
    				mindiff=diff;
    				closest=it2;
    				found=true;    			
    			}
    		} 
    	}
    	if (found)
    	{
		ok++;
    		found=true;
    		it->found=true;
    		closest->found=true;
    		meandeviationvector.push_back(closest->time-it->time);
	}
    }
    // FALSE NEGATIVES
    for (vector<onset>::iterator it=voriginal.begin(); it!=voriginal.end(); it++)
    {
    	if (it->found==false) fn++;
    }
    
    // FALSE POSITIVES
    for (vector<onset>::iterator it=vdetected.begin(); it!=vdetected.end(); it++)
    {
    	if (it->found==false) fp++;
    }

    // DOUBLED
    for (vector<onset>::iterator it=vdetected.begin(); it!=vdetected.end(); it++)
    {
    
       if (it->found==false) {
       	  bool found=false;
       	  for (vector<onset>::iterator it2=voriginal.begin() ; it2!=voriginal.end() && !found; it2++)
	  {       
		if (onsetOK(it2->time, it->time)) {
			doubled++;
			found=true;			
		}		
  	  }
      }
    }
    // MERGED 
    for (vector<onset>::iterator it=voriginal.begin(); it!=voriginal.end(); it++)
    {
       if (it->found==true) {
       	  bool found=false;
       	  for (vector<onset>::iterator it2=vdetected.begin() ; it2!=vdetected.end() && !found; it2++)
	  {    
		if (it2->found==false && onsetOK(it2->time, it->time)) {
			merged++;
			found=true;			
		}		
  	  }
      }
    }
    
    double prec=(float)ok/(float)(ok+fp);
    double rec=(float)ok/(float)(ok+fn);
    double fmeasure=(float)(2*prec*rec)/(float)(prec+rec);
    double mergedrate;
    if (fn!=0) mergedrate= 100.0*((float)merged/(float)fn);
    else mergedrate=0;
    double doubledrate;
    if (fp!=0) doubledrate= 100.0*((float)doubled/(float)fp);
    else doubledrate=0;
    
    double sumdev=0;
    for (vector<double>::iterator itd=meandeviationvector.begin(); itd!=meandeviationvector.end(); itd++)
    	sumdev+=*itd;
    double meandev=sumdev/(double)(meandeviationvector.size());

    cout << "OK= " << ok << endl << "FP= " << fp << endl << "FN= " << fn << endl;
    cout << "Doubled= " << doubled << endl << "Merged= " << merged << endl;
    cout << "MergedRate= " << mergedrate << endl << "DoubledRate= " << doubledrate << endl;
    cout << "MeanDeviation= " << meandev << endl;
    cout << "Prec= " << prec << endl << "Rec= " << rec << endl << "Fmeasure= " << fmeasure << endl;
}

int main(int argc, char *argv[]) {
	
	if (argc!=3) {
		cerr << "Sintax: " << argv[0] << " <ground-truth.txt> <detected_onsets.txt>" << endl;
		exit(-1);
	}

	ifstream inoriginal(argv[1]); 
	ifstream indetected(argv[2]);
	vector<onset> voriginal;
	vector<onset> vdetected;
	
	if (inoriginal.is_open())  
	{ 
 	   onset o;
  	   inoriginal >> o.time;
           while (!inoriginal.eof()) {
		o.found=false;
		voriginal.push_back(o);
		inoriginal >> o.time;
	  }
	  if (indetected.is_open()) { 	
		onset o;
		indetected >> o.time;
		while (indetected.good()) {
			o.found=false;
			vdetected.push_back(o);
			indetected >> o.time;
		}

	       // Compare the vectors read
	       Compare(voriginal,vdetected);
	   }
	   else cerr << "Error: file " << argv[2] << " not found\n";
	} 
	else cerr << "Error: file " << argv[1] << " not found\n";
}
