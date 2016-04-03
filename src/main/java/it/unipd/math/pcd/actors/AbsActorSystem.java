/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A map-based implementation of the actor system.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActorSystem implements ActorSystem {

    /**
     * Map Actor statica
     */
    private static final Map<ActorRef<?>, Actor<?>> actors = new ConcurrentHashMap<>();

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode) {

        // ActorRef instance
        ActorRef<?> reference;
        try {
            // Create the reference to the actor
            reference = this.createActorReference(mode);
            // Create the new instance of the actor
            Actor actorInstance = ((AbsActor) actor.newInstance()).setSelf(reference);
            // Associate the reference to the actor
            actors.put(reference, actorInstance);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoSuchActorException(e);
        }
        return reference;
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }

    protected abstract ActorRef createActorReference(ActorMode mode);

    @Override
    public void stop(ActorRef<?> ref) throws NoSuchActorException {
        AbsActor<?> actor = (AbsActor<?>) getActor(ref);

        if (actor != null) {
            actor.interrupt();
            killActor(ref);
        } else throw new NoSuchActorException("Actor not found!");
    }

    @Override
    public synchronized void stop() {
        Iterator it = actors.keySet().iterator();
        while (it.hasNext())
            stop((ActorRef<?>) it.next());
    }

    /**
     *
     *
     * @param ref ActorRef
     * @return Actor associated
     * @throws NoSuchActorException
     */
    public Actor getActor(ActorRef ref) throws NoSuchActorException {
        Actor actor = actors.get(ref);

        if (actor == null)
            throw new NoSuchActorException();
        else return actor;
    }

    public synchronized void killActor(ActorRef<? extends Message> ref) {
        AbsActor actor = (AbsActor) getActor(ref);

        while (!actor.isInterrupted()) {
            try {
                actor.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        actors.remove(ref);
    }

}