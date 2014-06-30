package org.ssascaling.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import org.ssascaling.Service;
import org.ssascaling.executor.VM;
import org.ssascaling.objective.Cost;
import org.ssascaling.objective.Objective;
import org.ssascaling.primitive.ControlPrimitive;
import org.ssascaling.primitive.HardwareControlPrimitive;
import org.ssascaling.primitive.Primitive;
import org.ssascaling.qos.QualityOfService;

/**
 * Only the collections here and the ones in Service class need to be maintained.
 * 
 * The collections here are read-only unless there is a deployment changes, which should not be
 * happen very often.
 * 
 * We assume that these collection can be updated via GMS when adding/removing VMs and service-instances.
 * @author tao
 *
 */
public class Repository {

	// ************** These are the collections contain unique instance of QoS and primitives, 
	// which need to be updated when adding newly measured data ***********
	
	// String name - services
	// We assume that the name here is unique even across different VMs.
	// VM_ID + '-' +  service name
	private static Map<String, Service> services
	 = new ConcurrentHashMap<String, Service>();
	
	// String VM_ID - VM
	private static Map<String, VM> vms  
		 = new ConcurrentHashMap<String, VM>();
	
	
	// Used to detect under-provisioning
	// need concurrent set
	private static Set<QualityOfService> qoss = new ConcurrentSkipListSet<QualityOfService>();
	// Used to detect over-provisioning
	private static Set<Cost> cost = new ConcurrentSkipListSet<Cost>();
	
	// ************** These are the collections contain unique instance of QoS and primitives, 
	// which need to be updated when adding newly measured data ***********
	
	// These are the objectives that managed by this super region control.
	// This is because any given objective need to have at least one direct primitive.
	// So this is basically a collection of all objectives.
	// Mainly for QoS
	
	// This can be configure the same time as possible primitives for services.
	private static Map<Objective, Set<Primitive>> directPrimitives = new ConcurrentHashMap<Objective, Set<Primitive>>();
	
	public static Set<Objective> getAllObjectives(){
		return directPrimitives.keySet();
	}
	
	public static Collection<Service> getAllServices(){
		return services.values();
	}
	
	public static Set<QualityOfService> getQoSSet(){
		return qoss;
	}
	
	public static void setQoS(QualityOfService qos){
		qoss.add(qos);
	}
	
	
	public static Set<Cost> getCostSet(){
		return cost;
	}
	
	public static void setVM (String VM_ID, VM vm) {
		vms.put(VM_ID, vm);
	}
	
	public static VM getVM (String VM_ID) {
		return vms.get(VM_ID);
	}
	
    public static boolean isContainVM (String VM_ID) {
		return vms.containsKey(VM_ID);
	}
	
	public static void setService (String name, Service service) {
		services.put(name, service);
	}
	
	public static Service getService (String name) {
		return services.get(name);
	}
	
    public static boolean isContainService (String name) {
		return services.containsKey(name);
	}
	
	public static void prepareToUpdateHardwareControlPrimitive(String VM_ID, String name, double... values) {
		if (values.length == 1) {
			vms.get(VM_ID).getHardwareControlPrimitive(name).prepareToAddValue(values[0]);
		} else {
			vms.get(VM_ID).getHardwareControlPrimitive(name).prepareToAddValue(values);
		}
		
		
	}
	
	public static void prepareToAddValueForQoS(String service, String name, double... values){
		services.get(service).prepareToUpdateQoSValue(name, values);
	}
	
	public static void prepareToAddValueForPrimitive(String service, String name, double... values){
		
		services.get(service).prepareToUpdatePrimitiveValue(name, values);
	}
	
	public static void setDirectForAnObjective  (Objective obj, Primitive p) {
		if (!directPrimitives.containsKey(obj)) {
			directPrimitives.put(obj, new ConcurrentSkipListSet<Primitive>());
		}
		
		directPrimitives.get(obj).add(p);
	}
	
	public static boolean isDirectForAnObjective (Objective obj, Primitive p) {
		return directPrimitives.get(obj).contains(p);
	}
}
