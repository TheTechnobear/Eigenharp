
import piw
from pi import agent, domain, bundles,atom
from . import soundplane_version as version

from .soundplane_plg_native import soundplane

#
# The OSC output agent
#
class Agent(agent.Agent):
    def __init__(self, address, ordinal):
        agent.Agent.__init__(self, signature=version, names='soundplane', ordinal=ordinal)

        self.domain = piw.clockdomain_ctl()

        self.domain.set_source(piw.makestring('*',0))

        self.soundplane = soundplane(self.domain, "localhost", 3123);
        self.output = self.soundplane.create_output("", True, 0)
        self.input = bundles.VectorInput(self.output, self.domain, signals=(1,2,3,4))

		# related inputs for a key
        self[1] = atom.Atom(names="inputs")
        self[1][1] = atom.Atom(names='frequency input', domain=domain.BoundedFloat(1,96000), policy=self.input.vector_policy(1,False))
#        self[1][1] = atom.Atom(domain=domain.Aniso(), policy=self.input.vector_policy(1,False), names='key input')
        self[1][2] = atom.Atom(domain=domain.BoundedFloat(0,1), policy=self.input.vector_policy(2,False), names='pressure input')
        self[1][3] = atom.Atom(domain=domain.BoundedFloat(-1,1), policy=self.input.vector_policy(3,False), names='roll input')
        self[1][4] = atom.Atom(domain=domain.BoundedFloat(-1,1), policy=self.input.vector_policy(4,False), names='yaw input')
        


		# breath output
        self.breath_output = self.soundplane.create_output("x1",False,1)
        self.breath_input = bundles.VectorInput(self.breath_output, self.domain,signals=(1,))
        self[1][5] = atom.Atom(domain=domain.BoundedFloat(0,1), policy=self.breath_input.vector_policy(1,False), names='breath input')

        # outputs for strips
        self.absstrip1_output = self.soundplane.create_output("x2",False,4)
        self.absstrip1_input = bundles.VectorInput(self.absstrip1_output, self.domain,signals=(1,))
        self[1][6] = atom.Atom(domain=domain.BoundedFloat(-1,1), policy=self.absstrip1_input.vector_policy(1,False), names='absolute strip input', ordinal=1)
        self.absstrip2_output = self.soundplane.create_output("x3",False,5)
        self.absstrip2_input = bundles.VectorInput(self.absstrip2_output, self.domain,signals=(1,))
        self[1][7] = atom.Atom(domain=domain.BoundedFloat(-1,1), policy=self.absstrip2_input.vector_policy(1,False), names='absolute strip input', ordinal=2)
        self.strippos1_output = self.soundplane.create_output("x5",False,2)
        self.strippos1_input = bundles.VectorInput(self.strippos1_output, self.domain,signals=(1,))
        self[1][8] = atom.Atom(domain=domain.BoundedFloat(-1,1), policy=self.strippos1_input.vector_policy(1,False), names='strip position input', ordinal=1)
        self.strippos2_output = self.soundplane.create_output("x4",False,3)
        self.strippos2_input = bundles.VectorInput(self.strippos2_output, self.domain,signals=(1,))
        self[1][9] = atom.Atom(domain=domain.BoundedFloat(-1,1), policy=self.strippos2_input.vector_policy(1,False), names='strip position input', ordinal=2)
        
        self[2] = atom.Atom(names="host")
        self[2][1] = atom.Atom(domain=domain.String(), init='localhost', names='name', policy=atom.default_policy(self.__set_host));
        self[2][2] = atom.Atom(domain=domain.BoundedInt(1,9999), init=3123, names='port', policy=atom.default_policy(self.__set_port), )

        self[3] = atom.Atom(domain=domain.BoundedInt(1,1000), init=250, policy=atom.default_policy(self.__set_data_freq), names='data frequency')
        self[4] = atom.Atom(domain=domain.BoundedFloat(0,24), init=0, policy=atom.default_policy(self.__set_pitch_bend), names='pitch bend range')
        self[5] = atom.Atom(domain=domain.BoundedInt(1,16), init=16, policy=atom.default_policy(self.__set_max_voice_count), names='max voice count')
        self[6] = atom.Atom(domain=domain.Bool(),init=False,policy=atom.default_policy(self.__set_kyma_mode),names='kyma')
        print "connect ", self[2][1].get_value(), " port ", self[2][2].get_value()
        self.soundplane.connect(self[2][1].get_value(), self[2][2].get_value())
        

    def __set_data_freq(self,value):
        self[3].set_value(value)
        self.soundplane.set_data_freq(value)
        return True

    def __set_pitch_bend(self,value):
        self[4].set_value(value)
        self.soundplane.set_pitch_bend(value)
        return True

    def __set_max_voice_count(self,value):
        self[5].set_value(value)
        self.soundplane.set_max_voice_count(value)
        return True

    def __set_kyma_mode(self,value):
        self[6].set_value(value)
        self.soundplane.set_kyma_mode(value)
        return True

    def __set_host(self,value):
        self[2][1].set_value(value)
        self.soundplane.connect(self[2][1].get_value(), self[2][2].get_value())
        return True

    def __set_port(self,value):
        self[2][2].set_value(value)
        self.soundplane.connect(self[2][1].get_value(), self[2][2].get_value())
        return True

#
# Define Agent as this agents top level class
#
agent.main(Agent)
