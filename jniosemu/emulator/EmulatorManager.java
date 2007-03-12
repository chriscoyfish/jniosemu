package jniosemu.emulator;

import java.util.ArrayList;

import jniosemu.events.EventManager;
import jniosemu.events.Events;
import jniosemu.events.EventObserver;
import jniosemu.emulator.compiler.Compiler;
import jniosemu.emulator.compiler.CompilerException;
import jniosemu.emulator.memory.MemoryManager;
import jniosemu.emulator.register.RegisterManager;
import jniosemu.instruction.InstructionManager;
import jniosemu.instruction.emulator.Instruction;

/**
 * Managing the emulation
 */
public class EmulatorManager implements EventObserver
{
	private EventManager eventManager;
	/**
	 * PC address
	 */
	private int pc = MemoryManager.PROGRAMSTART;
	/**
	 * MemoryManager that is used
	 */
	private MemoryManager memory;
	/**
	 * RegisterManager that is used
	 */
	private RegisterManager register = new RegisterManager();
	/**
	 * InstructionManager that is used
	 */
	private InstructionManager instructions = new InstructionManager();
	/**
	 * Emulator that is used
	 */
	private Emulator emulator;
	/**
	 * True if the emulator is running
	 */
	private boolean running = false;

	private Program program;

	/**
	 * Init EmulatorManager
	 *
	 * @post Emulator, memory and pc is set
	 * @calledby JNiosEmu.main()
	 * @calls Emulator(), MemoryManager()
	 */
	public EmulatorManager(EventManager eventManager) {
		this.emulator = new Emulator(this);
		this.eventManager = eventManager;

		String[] events = {Events.EVENTID_COMPILE, Events.EVENTID_RUN, Events.EVENTID_STEP, Events.EVENTID_PAUSE, Events.EVENTID_RESET};
		eventManager.addEventObserver(events, this);
	}

	/**
	 * Return the PC address
	 *
	 * @calledby Emulator
	 *
	 * @return PC address
	 */
	public int readPC() {
		return this.pc;
	}

	/**
	 * Return the PC address
	 *
	 * @calledby Emulator
	 *
	 * @param value  PC address value
	 * @throws EmulatorException  If value modulus 4 is equal to 0
	 */
	public void writePC(int value) throws EmulatorException {
		if (value % 4 != 0)
			throw new EmulatorException();

		this.pc = value;
	}

	/**
	 * Run instructions as long as there are any or program is stopped
	 *
	 * @calledby update()
	 * @calls step()
	 *
	 * @throws EmulatorException  If something goes wrong when trying to run a instruction
	 */
	public void run() throws EmulatorException {
		while (this.step());
	}

	/**
	 * Run next instruction
	 *
	 * @post update pc to next instruction
	 * @calledby update(), run()
	 * @calls MemoryManager.readInt(), InstructionManager.get(), Instruction.run()
	 *
	 * @throws EmulatorException  If it can't get an opcode from MemoryManager, an instruction from InstructionManager or run() on Instruction
	 */	
	public boolean step() throws EmulatorException {
		int opCode = this.memory.readInt(this.pc);
		if (opCode == 0)
			return false;

		Instruction instruction = this.instructions.get(opCode);
		instruction.run(this.emulator);
		this.pc += 4;

		return true;
	}

	/**
	 * Helpfunction that returns the current MemoryManager
	 *
	 * @calledby Emulator
	 */
	public MemoryManager getMemoryManager() {
		return this.memory;
	}

	/**
	 * Helpfunction that returns the current RegisterManager
	 *
	 * @calledby Emulator
	 */
	public RegisterManager getRegisterManager() {
		return this.register;
	}

	public Program getProgram() {
		return this.program;
	}

	public void dump() {
		this.register.dump();
		this.memory.dump();
	}

	/**
	 * Compile the sourcecode that exists in the editor
	 *
	 * @calledby update()
	 * @calls Compiler(), Compiler.compile(), Compiler.link()
	 */
	public void compile(String lines) {
		Compiler compiler = new Compiler(lines);

		this.program = null;
		try {
			compiler.compile();
			this.program = compiler.link();
		} catch (CompilerException e) {
			System.out.println("CompilerException: "+ e.getMessage());
		}

		this.memory = new MemoryManager(program.getData());
		this.pc = program.getStartAddr();

		this.eventManager.sendEvent(Events.EVENTID_COMPILATION_DONE, this.program);
	}

	/**
	 * Pause the emulation
	 *
	 * @calledby update()
	 */
	public void pause() {
		
	}

	/**
	 * Reset the emulation
	 *
	 * @calledby update()
	 * @calls RegisterManager.reset(), MemoryManager.reset()
	 */
	public void reset() {
		
	}

	/**
	 * Listen for events and acts on them
	 *
	 * @calledby EVENTID_COMPILE, EVENTID_RUN, EVENTID_PAUSE, EVENTID_STEP, EVENTID_RESET
	 * @calls compile(), run(), pause(), step(), reset()
	 */
	public void update(String eventIdentifier, Object obj) {
		if (eventIdentifier.equals(Events.EVENTID_COMPILE)) {
			this.compile((String)obj);
		} else if (eventIdentifier.equals(Events.EVENTID_STEP)) {
			// this.step();
		} else if (eventIdentifier.equals(Events.EVENTID_RUN)) {
			// this.run();
		} else if (eventIdentifier.equals(Events.EVENTID_PAUSE)) {
			this.pause();
		} else if (eventIdentifier.equals(Events.EVENTID_RESET)) {
			this.reset();
		}
	}
}
