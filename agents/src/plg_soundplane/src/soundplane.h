
#ifndef __SOUNDPLANE__
#define __SOUNDPLANE__

#include "soundplane_plg_exports.h"

#include <piw/piw_bundle.h>
#include <piw/piw_data.h>
#include <piw/piw_clock.h>

namespace soundplane_plg
{
    class SOUNDPLANE_PLG_DECLSPEC_CLASS soundplane_server_t : public pic::nocopy_t // cant copy this
    {
        public:
            soundplane_server_t(piw::clockdomain_ctl_t *, const std::string &a, unsigned p);
            ~soundplane_server_t();

            piw::cookie_t create_output(const std::string &prefix, bool fake_key, unsigned signals);
            void connect(const std::string &a, unsigned p);
            void set_max_voice_count(unsigned);
            void set_data_freq(unsigned);
            void set_pitch_bend(float);
            void set_kyma_mode(bool);

            class impl_t;
            
        private:
            impl_t *impl_;

	};
}

#endif

