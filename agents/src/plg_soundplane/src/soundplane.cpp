#include "soundplane.h"
#include <piw/piw_keys.h>
#include <picross/pic_float.h>
#include <picross/pic_stdint.h>

#include <lib_lo/lo/lo.h>
#include <map>

#include "Madrona/SoundplaneOSCOutput.h"

#ifndef UINT64_MAX
#define UINT64_MAX  (18446744073709551615ULL)
#endif


#define IN_FREQ 1
#define IN_PRESSURE 2
#define IN_ROLL 3
#define IN_YAW 4

#define IN_MASK SIG4(IN_FREQ,IN_PRESSURE,IN_ROLL,IN_YAW)


// hardcode in soundplane to be once a second
#define INFREQUENT_TIME 1000*1000

namespace
{

    struct soundplane_wire_t;

    struct soundplane_t: piw::root_t
    {
        soundplane_t(soundplane_plg::soundplane_server_t::impl_t *server, const std::string &prefix, bool key_, unsigned ident);
        ~soundplane_t();
        piw::wire_t *root_wire(const piw::event_data_source_t &);

        void root_opened();
        void root_closed();
        void root_clock();

        void root_latency();

        piw::cookie_t cookie() { return piw::cookie_t(this); }

        soundplane_plg::soundplane_server_t::impl_t *server_;

        std::string prefix_;
        bool key_;
        unsigned ident_;
        bct_clocksink_t *upstream_;

        std::vector<soundplane_wire_t *> wires_;
    };

    struct soundplane_wire_t:
        piw::wire_t,
        piw::event_data_sink_t,
        pic::element_t<>,
        virtual pic::lckobject_t
    {
        soundplane_wire_t(soundplane_t *output, unsigned index, const piw::event_data_source_t &es);
        ~soundplane_wire_t();

        int voiceId() { return voiceId_;}
        void voiceId(int v) { voiceId_=v;}
        unsigned long long voiceTime() { return voiceTime_;}
        void voiceTime(unsigned long long t) { voiceTime_=t;}


        void wire_closed();

        void ticked(unsigned long long from, unsigned long long to);
        void event_start(unsigned seq, const piw::data_nb_t &id,const piw::xevent_data_buffer_t &b);
        bool event_end(unsigned long long);
        void event_buffer_reset(unsigned,unsigned long long, const piw::dataqueue_t &,const piw::dataqueue_t &);

        void send(unsigned long long time, unsigned signal, bool isStart);
        SoundplaneOSCOutput * soundplane();

        soundplane_t *output_;

        unsigned index_;

        piw::xevent_data_buffer_t::iter_t iterator_;

        piw::dataholder_nb_t id_string_;

        unsigned long long last_processed_;
        int voiceId_;
        unsigned long long voiceTime_;
    };
};


struct soundplane_plg::soundplane_server_t::impl_t:
    piw::clocksink_t
{
    impl_t(piw::clockdomain_ctl_t *d, const std::string &a, const std::string &p);
    ~impl_t();

    void clocksink_ticked(unsigned long long f, unsigned long long t);

    piw::cookie_t create_output(const std::string &prefix, bool key, unsigned signals);

    float pitchBend() { return pitchBend_;}
    void pitchBend(float f) { pitchBend_=f;}

    void deactivate_wire_slow(soundplane_wire_t *w);
    void activate_wire_fast(soundplane_wire_t *w);
    void deactivate_wire_fast(soundplane_wire_t *w);

    std::map<std::string,soundplane_t *> outputs_;
    SoundplaneOSCOutput * soundplane_;
    pic::ilist_t<soundplane_wire_t> active_wires_;
    std::vector<soundplane_wire_t *> voices_;
    float pitchBend_;
	std::string host_;
	int port_;
	unsigned long long int last_connect_time_;
	unsigned long long int last_tick_;
	unsigned long long int last_infreq_;
};

#define SDM_T_VOICE 0
#define SDM_T_X 1
#define SDM_T_Y 2
#define SDM_T_Z 3
#define SDM_T_DZ 4
#define SDM_T_NOTE 5

#define SDM_C_ZONEID 0
#define SDM_C_X 5
#define SDM_C_Y 6
#define SDM_C_Z 7



bool sendSoundplaneMessage( SoundplaneOSCOutput *pOut,MLSymbol type)
{
	if(! pOut->isActive()) return false;

	SoundplaneDataMessage msg;
	msg.mType = type;
	msg.mZoneName=nullZone.c_str();

	pOut->processMessage(&msg);
	return true;
}



bool sendSoundplaneTouchMessage( SoundplaneOSCOutput *pOut,
							MLSymbol type, MLSymbol subtype,
							int voice,
							float note, float x, float y, float z)
{
	if(! pOut->isActive()) return false;

	SoundplaneDataMessage msg;
	msg.mType = type;
	msg.mSubtype = subtype;
	msg.mData[SDM_T_VOICE] = voice;
	msg.mData[SDM_T_NOTE] = note;
	msg.mData[SDM_T_X] = x;
	msg.mData[SDM_T_Y] = y;
	msg.mData[SDM_T_Z] = z;
	msg.mData[SDM_T_DZ] = z;
	msg.mZoneName=nullZone.c_str();

//	pic::logmsg() << "sendSoundplaneTouchMessage"
//					<< " type:" << type
//					<< " subtype:" << subtype
//					<< " voice:" << voice
//					<< " x:" << x
//					<< " y:" << y
//					<< " z:" << z
//					<< " note:" << note;

	pOut->processMessage(&msg);
	return true;
}

bool sendSoundplaneControlMessage( SoundplaneOSCOutput *pOut,
							MLSymbol type, MLSymbol subtype,int zoneId, float x, float y, float z, const char* zone)
{
	if(! pOut->isActive()) return false;

//	pic::logmsg() << "sendSoundplaneControlMessage"
//					<< " type:" << type
//					<< " zoneId:" << zoneId
//					<< " subtype:" << subtype
//					<< " x:" << x
//					<< " y:" << y
//					<< " z:" << z
//					<< " zone:" << zone;


	SoundplaneDataMessage msg;
	msg.mType = type;
	msg.mSubtype = subtype;
	msg.mData[SDM_C_ZONEID]= zoneId;
	msg.mData[SDM_C_X] = x;
	msg.mData[SDM_C_Y] = y;
	msg.mData[SDM_C_Z] = z;
	msg.mZoneName=zone;


	pOut->processMessage(&msg);
	return true;
}



soundplane_wire_t::soundplane_wire_t(soundplane_t *output, unsigned index, const piw::event_data_source_t &es):
		output_(output), index_(index), last_processed_(0), voiceId_(-1), voiceTime_(0)
{
    output_->wires_[index_] = this;

    subscribe_and_ping(es);
}

soundplane_wire_t::~soundplane_wire_t()
{
    // disconnect this wire.
    disconnect();

    // unsubscribe the event data sink
    unsubscribe();

    // get us out of the active wire list
    output_->server_->deactivate_wire_slow(this);

    // remove from the output
    output_->wires_[index_] = 0;

}

void soundplane_wire_t::event_start(unsigned seq, const piw::data_nb_t &id, const piw::xevent_data_buffer_t &b)
{
    output_->server_->activate_wire_fast(this);
    iterator_ = b.iterator();

    pic::msg_t msg;
    msg << id;
    id_string_.set_nb(piw::makestring_nb(msg.str().c_str(),id.time()));

    ::sendSoundplaneMessage(soundplane(),MLS_startFrameSym);
    send(id.time(),1,true);
    ::sendSoundplaneMessage(soundplane(),MLS_endFrameSym);

}

void soundplane_wire_t::ticked(unsigned long long from, unsigned long long to)
{
    piw::data_nb_t d;
    unsigned s=1;

	if(voiceId()<0)
	{
		// voice has been deallocated clear all incoming events
		iterator_->reset_all(to);
		return;
	}

    send(to,s,false);
}

SoundplaneOSCOutput * soundplane_wire_t::soundplane()
{
	return output_->server_->soundplane_;
}


void soundplane_wire_t::send(unsigned long long t,unsigned s, bool isStart)
{

    piw::data_nb_t d;
	MLSymbol type =MLS_nullSym, subtype = MLS_nullSym;
    if(output_->key_)
    {
    	int voice = 0;
		float note = 0.0f;
		float x=0.0f,y=0.0f,z=0.0f;

		float pb_range=output_->server_->pitchBend();

		if(iterator_->latest(IN_PRESSURE,d,t))
		{
//	    	pic::logmsg() << "p" << d;
			z= d.as_denorm_float();
		}
		if(iterator_->latest(IN_ROLL,d,t))
		{
//	    	pic::logmsg() << "y" << d;
			y = d.as_denorm_float();
		}
		if(iterator_->latest(IN_YAW,d,t))
		{
//	    	pic::logmsg() << "x" << d;
			x = d.as_denorm_float();
		}

		if(iterator_->latest(IN_FREQ,d,t))
		{
			voice=voiceId_;
			float freq = d.as_denorm_float();
			note = 12.0f * pic_log2(freq/440.0f) + 69.0f + (pb_range * y);
			type=MLS_touchSym;
			if(isStart)
			{
				subtype = MLS_onSym;
			}
			else
			{
				subtype = MLS_continueSym;
			}
//	    	pic::logmsg() << "freq" << freq << " note" << note;
		}

		iterator_->reset(IN_FREQ,t+1);
		iterator_->reset(IN_PRESSURE,t+1);
		iterator_->reset(IN_ROLL,t+1);
		iterator_->reset(IN_YAW,t+1);

		::sendSoundplaneTouchMessage(soundplane(),type,subtype,voice,note,x,y,z);
    }
	else
	{
		type = MLS_controllerSym;
		subtype = MLS_xSym;
		float x=0.0f,y=0.0f,z=0.0f;
		int zoneId=output_->ident_;
		const char* zone =  output_->prefix_.c_str();

		if(iterator_->latest(1,d,t))
		{
			x = d.as_denorm_float();
		}

		::sendSoundplaneControlMessage(soundplane(),type,subtype,zoneId,x,y,z,zone);
		if(s>0) iterator_->reset(s,t+1);
	}

    
    // store the last processing time for each wire
    last_processed_ = t;
}


bool soundplane_wire_t::event_end(unsigned long long t)
{
	// turn off, if the voice has not already been deallocated
	if(voiceId()>=0 && output_->key_)
	{
		::sendSoundplaneMessage(soundplane(),MLS_startFrameSym);
	    send(t,1,false);
	    if (output_->key_)
	    {
	        ::sendSoundplaneTouchMessage(soundplane(),MLS_touchSym,MLS_offSym,voiceId_,0,0.0f,0.0f,0.0f);
	    }

	    ::sendSoundplaneMessage(soundplane(),MLS_endFrameSym);
	}

    id_string_.clear_nb();
    iterator_.clear();
    output_->server_->deactivate_wire_fast(this);

    return true;
}


void soundplane_wire_t::event_buffer_reset(unsigned s,unsigned long long t, const piw::dataqueue_t &oq,const piw::dataqueue_t &nq)
{
    iterator_->set_signal(s,nq);
    iterator_->reset(s,t);
    send(t,s,false);
}

void soundplane_wire_t::wire_closed()
{
    delete this;
}

soundplane_t::soundplane_t(soundplane_plg::soundplane_server_t::impl_t *server, const std::string &prefix, bool key, unsigned ident):
  piw::root_t(0), server_(server), prefix_(prefix), key_(key), ident_(ident), upstream_(0)
{
    // add ourself to the list of outputs in the server.
    server_->outputs_.insert(std::make_pair(prefix_,this));
}

soundplane_t::~soundplane_t()
{
    tracked_invalidate();
    disconnect();

    server_->outputs_.erase(prefix_);

    for(unsigned i=0;i<wires_.size();i++)
    {
        if(wires_[i])
        {
            delete wires_[i];
        }
    }
}

piw::wire_t *soundplane_t::root_wire(const piw::event_data_source_t &es)
{
    unsigned i = 0;
    for(i=0;i<wires_.size();i++)
    {
        if(!wires_[i])
        {
            goto found_slot;
        }
    }
    wires_.resize(i+1);
    wires_[i] = 0;

found_slot:
    return new soundplane_wire_t(this,i,es);
}

void soundplane_t::root_closed()
{
    delete this;
}


void soundplane_t::root_clock()
{
    bct_clocksink_t *c = get_clock();

    if(c!=upstream_)
    {
        if(upstream_)
        {
            server_->remove_upstream(upstream_);
        }

        upstream_=c;

        if(upstream_)
        {
            server_->add_upstream(upstream_);
        }
    }
}


void soundplane_t::root_opened()
{
    root_clock();
}

void soundplane_t::root_latency()
{
}

soundplane_plg::soundplane_server_t::impl_t::impl_t(piw::clockdomain_ctl_t *d, const std::string &a, const std::string &p) :
		pitchBend_(0)
{
	soundplane_ = new SoundplaneOSCOutput();

	soundplane_->setDataFreq(250.0f);
	soundplane_->setKymaMode(false);
	soundplane_->setSerialNumber(0x123);
	soundplane_->setMaxTouches(kSoundplaneMaxTouches);

	soundplane_->initialize();
	host_=a;
	port_=atoi(p.c_str());
	if(soundplane_->connect(host_.c_str(),port_))
	{
		soundplane_->setActive(true);
		pic::logmsg() << "soundplane connected :" << host_ << ":" << port_;
		soundplane_->notify(true);
	}
	else
	{
		soundplane_->setActive(false);
		pic::logmsg() << "soundplane unable to connect :" << host_ << ":" << port_;
	}

	last_connect_time_=piw::tsd_time();
	last_tick_=piw::tsd_time();
	last_infreq_=piw::tsd_time();

    d->sink(this,"soundplane server");

    tick_enable(false); // dont suppress ticks

}

soundplane_plg::soundplane_server_t::impl_t::~impl_t()
{
	if(soundplane_->isActive())
	{
		soundplane_->notify(false);
		soundplane_->setActive(false);
	}
	delete soundplane_;

    tick_disable();
    close_sink();
    std::map<std::string,soundplane_t *>::iterator i;

    while((i=outputs_.begin()) != outputs_.end())
    {
        delete i->second;
    }
}

piw::cookie_t soundplane_plg::soundplane_server_t::impl_t::create_output(const std::string &prefix, bool key, unsigned ident)
{
    std::map<std::string,soundplane_t *>::iterator i;

    // check for a duplicate output.
    // return a null cookie in that case (an event bucket)
    if((i=outputs_.find(prefix))!=outputs_.end())
    {
        return piw::cookie_t();
    }

    soundplane_t *o = new soundplane_t(this,prefix,key,ident);
    return o->cookie();
}


static int remover__(void *impl__, void *wire__)
{
    soundplane_plg::soundplane_server_t::impl_t *impl = (soundplane_plg::soundplane_server_t::impl_t *)impl__;
    soundplane_wire_t *wire = (soundplane_wire_t *)wire__;
    impl->active_wires_.remove(wire);
    return 0;
}

void soundplane_plg::soundplane_server_t::impl_t::deactivate_wire_slow(soundplane_wire_t *w)
{
    piw::tsd_fastcall(remover__,this,w);
}

void soundplane_plg::soundplane_server_t::impl_t::activate_wire_fast(soundplane_wire_t *w)
{
    unsigned i = 0;
    unsigned oldestVoice = 0;
    unsigned long long oldestTime = UINT64_MAX;

    // dont need voice allocation on non-keys
    if(w->output_->key_ == false)
    {
    	w->voiceId(1000);
    	w->voiceTime(0);
        active_wires_.append(w);
        return;
    }


    //find an empty slot, up to maxium... increase vector if needed, or reallocate voices if necessary
    for(i=0;i<voices_.size() && i < (unsigned) soundplane_->getMaxTouches() ;i++)
    {
        if(!voices_[i])
        {
            goto found_slot;
        }
        if (voices_[i]->voiceTime() < oldestTime)
        {
        	oldestVoice=i;
        	oldestTime = voices_[i]->voiceTime();
        }
    }
    if(i < (unsigned) soundplane_->getMaxTouches() )
    {
    	voices_.resize(i+1);
    	voices_[i] = 0;
    }
    else
    {
    	// hit max voice so reallocate
    	i=oldestVoice;
	    sendSoundplaneMessage(soundplane_,MLS_startFrameSym);
        sendSoundplaneTouchMessage(soundplane_,MLS_touchSym,MLS_offSym,i,0.0f,0.0f,0.0f,0.0f);
	    sendSoundplaneMessage(soundplane_,MLS_endFrameSym);
		voices_[i]->voiceId(-1);
    }

found_slot:
    voices_[i] = w;
    unsigned long long t=pic_microtime();
    w->voiceTime(t);
	w->voiceId(i);
//	pic::logmsg() << "allocate voice:" << i;
    active_wires_.append(w);
}

void soundplane_plg::soundplane_server_t::impl_t::deactivate_wire_fast(soundplane_wire_t *w)
{
    if(w->output_->key_ == false)
    {
    	w->voiceId(0);
    	w->voiceTime(0);
        active_wires_.remove(w);
    	return;
    }


	int v=w->voiceId();
	voices_[v]=0;
	w->voiceId(0);
	w->voiceTime(0);
//	pic::logmsg() << "deallocate voice:" << v;
    active_wires_.remove(w);
}


void soundplane_plg::soundplane_server_t::impl_t::clocksink_ticked(unsigned long long f, unsigned long long t)
{
    const unsigned long long dataTime = 1000*1000 / (unsigned long long) soundplane_->getDataFreq();

    if (t > (last_tick_ + dataTime))
	{
		static const long retryTime = 15*1000*1000; // try to reconnect every 15 seconds
		if (!soundplane_->isActive())
		{
			if(t > last_connect_time_+retryTime)
			{
				last_connect_time_=t;
				if(soundplane_->connect(host_.c_str(),port_))
				{
					soundplane_->setActive(true);
					pic::logmsg() << "soundplane connected :" << host_ << ":" << port_;
					soundplane_->notify(true);
				}
				else
				{
					soundplane_->setActive(false);
					pic::logmsg() << "soundplane unable to connect :" << host_ << ":" << port_;
					return;
				}
			}
			else
			{
				return;
			}
		}


		sendSoundplaneMessage(soundplane_, MLS_startFrameSym);

		soundplane_wire_t *w;
		for(w=active_wires_.head(); w!=0; w=active_wires_.next(w))
		{
			w->ticked(f,t);
		}
		sendSoundplaneMessage(soundplane_, MLS_endFrameSym);

	    if (t > (last_infreq_ + (INFREQUENT_TIME) ))
	    {
	    	soundplane_->doInfrequentTasks();
	    	last_infreq_ = t;
	    }

		last_tick_=t;
	}
}

/*
 * Static methods that can be called from the fast thread.
 */

static int __set_data_freq(void *i_, void *d_)
{
    soundplane_plg::soundplane_server_t::impl_t *i = (soundplane_plg::soundplane_server_t::impl_t *)i_;
    unsigned d = *(unsigned *)d_;
    i->soundplane_->setDataFreq(d);
    return 0;
}
static int __set_pitch_bend(void *i_, void *d_)
{
    soundplane_plg::soundplane_server_t::impl_t *i = (soundplane_plg::soundplane_server_t::impl_t *)i_;
    float d = *(float *)d_;
    i->pitchBend(d);
    return 0;
}

static int __set_max_voice_count(void *i_, void *d_)
{
    soundplane_plg::soundplane_server_t::impl_t *i = (soundplane_plg::soundplane_server_t::impl_t *)i_;
    unsigned d = *(unsigned *)d_;
    i->soundplane_->setMaxTouches(d);
    return 0;
}

static int __set_kyma_mode(void *i_, void *d_)
{
    soundplane_plg::soundplane_server_t::impl_t *i = (soundplane_plg::soundplane_server_t::impl_t *)i_;
    bool d = *(bool *)d_;
    i->soundplane_->setKymaMode(d);
    return 0;
}


soundplane_plg::soundplane_server_t::soundplane_server_t(piw::clockdomain_ctl_t *d, const std::string &a, const std::string &p): impl_(new impl_t(d,a,p))
{
}

soundplane_plg::soundplane_server_t::~soundplane_server_t()
{
    delete impl_;
}

piw::cookie_t soundplane_plg::soundplane_server_t::create_output(const std::string &prefix,bool key,unsigned ident)
{
    return impl_->create_output(prefix,key,ident);
}


void soundplane_plg::soundplane_server_t::set_max_voice_count(unsigned max_voice_count)
{
    piw::tsd_fastcall(__set_max_voice_count,impl_,&max_voice_count);
}

void soundplane_plg::soundplane_server_t::set_data_freq(unsigned dataFreq)
{
    piw::tsd_fastcall(__set_data_freq,impl_,&dataFreq);
}

void soundplane_plg::soundplane_server_t::set_pitch_bend(float pitchBend)
{
    piw::tsd_fastcall(__set_pitch_bend,impl_,&pitchBend);
}

void soundplane_plg::soundplane_server_t::set_kyma_mode(bool kyma_mode)
{
    piw::tsd_fastcall(__set_kyma_mode,impl_,&kyma_mode);
}

