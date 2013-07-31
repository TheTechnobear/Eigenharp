

import piw
from pi import agent, domain, bundles,atom
from . import scale_illuminator_version as version

from .scale_illuminator_plg_native import scale_illuminator

OUT_LIGHT=1

class Agent(agent.Agent):
    def __init__(self, address, ordinal):
        #
        agent.Agent.__init__(self, signature=version, names='scale illuminator', ordinal=ordinal)

        self.domain = piw.clockdomain_ctl()

        self.domain.set_source(piw.makestring('*',0))

        self[1] = atom.Atom(names='outputs')
        self[1][1] = bundles.Output(OUT_LIGHT, False, names='light output', protocols='revconnect')
        self.output = bundles.Splitter(self.domain, self[1][1])
        self.illuminator = scale_illuminator(self.domain, self.output.cookie())
        self.ctlr_fb = piw.functor_backend(1,True)
        self.ctlr_fb.set_functor(piw.pathnull(0),self.illuminator.control())
        self.ctlr_input = bundles.ScalarInput(self.ctlr_fb.cookie(),self.domain,signals=(1,))

        self[2]=atom.Atom(names='inputs')
        self[2][1] =atom.Atom(domain=domain.Aniso(),policy=self.ctlr_input.policy(1,False),names='controller input')
        self[2][2] = atom.Atom(domain=domain.String(), policy=atom.default_policy(self.__change_scale), names='scale')
        self[2][3] = atom.Atom(domain=domain.BoundedFloatOrNull(0,12),init=None,policy=atom.default_policy(self.__change_tonic),names='tonic')
        self[2][4] = atom.Atom(domain=domain.Bool(),init=False,policy=atom.default_policy(self.__change_inverted),names='inverted')
        self[2][5] = atom.Atom(domain=domain.Bool(),init=True,policy=atom.default_policy(self.__change_root_light),names='root')

    def __change_scale(self,value):
        self[2][2].set_value(value)
        self.illuminator.reference_scale(value);
        return True

    def __change_tonic(self,value):
        self[2][3].set_value(value)
        self.illuminator.reference_tonic(value);
        return True
                
    def __change_inverted(self,value):
        self[2][4].set_value(value)
        self.illuminator.inverted(value);
        return True
    
    def __change_root_light(self,value):
        self[2][5].set_value(value)
        self.illuminator.root_light(value);
        return True


#
# Define Agent as this agents top level class
#
agent.main(Agent)
